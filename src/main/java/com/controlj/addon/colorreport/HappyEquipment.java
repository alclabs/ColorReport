/*=============================================================================
                    AUTOMATED LOGIC CORPORATION
            Copyright (c) 1999 - 2003 All Rights Reserved
     This document contains confidential/proprietary information.
===============================================================================

   @(#)HappyEquipment

   Author(s) jmurph
   $Log: $    
=============================================================================*/
package com.controlj.addon.colorreport;

public class HappyEquipment implements Comparable<HappyEquipment>
{
   public String trendPath;
   public String eqDisplayPath;
   public long unhappyPercent;

   HappyEquipment(String trendPath, String eqDisplayPath, int unhappyPercent)
   {
      this.trendPath = trendPath;
      this.eqDisplayPath = eqDisplayPath;
      this.unhappyPercent = unhappyPercent;
   }

   public int compareTo(HappyEquipment o)
   {
      long otherHappyTime = o.unhappyPercent;
      if (unhappyPercent < otherHappyTime)
         return 1;
      else if (unhappyPercent == otherHappyTime)
         return 0;
      else
         return -1;
   }
}

