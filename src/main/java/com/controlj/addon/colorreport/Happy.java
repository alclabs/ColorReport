/*=============================================================================
                    AUTOMATED LOGIC CORPORATION
            Copyright (c) 1999 - 2003 All Rights Reserved
     This document contains confidential/proprietary information.
===============================================================================

   @(#)Happy

   Author(s) jmurph
   $Log: $    
=============================================================================*/
package com.controlj.addon.colorreport;

import com.controlj.green.addonsupport.InvalidConnectionRequestException;
import com.controlj.green.addonsupport.access.*;
import com.controlj.green.addonsupport.access.aspect.DigitalTrendSource;
import com.controlj.green.addonsupport.access.trend.*;
import com.controlj.green.addonsupport.access.util.Acceptors;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class Happy
{
   private SystemConnection system;

   public Happy(HttpServletRequest request) throws InvalidConnectionRequestException
   {
      system = DirectAccess.getDirectAccess().getUserSystemConnection(request);
   }

   public Collection<HappyEquipment> determineUnhappiness(final String tsName, final Date startDate,
                                                          final Date endDate, final String startLocation)
         throws SystemException, ActionExecutionException
   {
      final TrendRange range = TrendRangeFactory.byDateRange(startDate, endDate);
      final long timeDiff = endDate.getTime() - startDate.getTime();

      final List<HappyEquipment> happyEqs = new ArrayList<HappyEquipment>();
      system.runReadAction(FieldAccessFactory.newFieldAccess(), new ReadAction()
      {
         public void execute(SystemAccess systemAccess) throws Exception
         {
            Location start = systemAccess.getGeoRoot().getDescendant(startLocation);
            Collection<DigitalTrendSource> sources = start.find(DigitalTrendSource.class,
                                                                Acceptors.enabledTrendSourceByName(tsName));
            for (DigitalTrendSource source : sources)
            {
               TrendData<TrendDigitalSample> tdata = source.getTrendData(range);
               UnhappyTimeProcessor processor = tdata.process(new UnhappyTimeProcessor());
               int unhappyPercent = processor.getPercentUnhappy(timeDiff);

               String eqPath = source.getLocation().getTransientLookupString();
               String eqDisplayPath = source.getLocation().getParent().getDisplayPath();
               
               happyEqs.add(new HappyEquipment(eqPath, eqDisplayPath, unhappyPercent));
            }
         }
      });

      Collections.sort(happyEqs);
      return happyEqs;
   }

   private static class UnhappyTimeProcessor implements TrendProcessor<TrendDigitalSample>
   {
      private boolean lastState;
      private long lastTransitionTime;
      private long unhappyTime = 0;
      private long missingTime = 0;

      public int getPercentUnhappy(long timeRange)
      {
         return (int) ((unhappyTime *100L) / (timeRange - missingTime));
      }

      public void processStart(Date startTime, TrendDigitalSample startBookend)
      {
         lastState = startBookend != null && startBookend.getState();
         lastTransitionTime = startTime.getTime();
      }

      public void processData(TrendDigitalSample sample)
      {
         if (lastState)
            unhappyTime += sample.getTimeInMillis() - lastTransitionTime;

         lastState = sample.getState();
         lastTransitionTime = sample.getTimeInMillis();
      }

      public void processHole(Date start, Date end)
      {
         missingTime += (end.getTime() - start.getTime());
         lastTransitionTime = end.getTime();
      }

      public void processEnd(Date endTime, TrendDigitalSample endBookend)
      {
         // handle from the last sample until midnight
         if (lastState)
            unhappyTime += endTime.getTime() - lastTransitionTime;
      }
   }
   
   public boolean isPathValid(final String pathString)
   {
      boolean result = false;
      try
      {
         system.runReadAction(new ReadAction()
         {
            public void execute(SystemAccess systemAccess) throws Exception
            {
               systemAccess.getTree(SystemTree.Geographic).getRoot().getDescendant(pathString);
            }
         });
         result = true;
      }
      catch (ActionExecutionException ignored) { }
      catch (SystemException ignored) { }
      
      return result;
   }
}

