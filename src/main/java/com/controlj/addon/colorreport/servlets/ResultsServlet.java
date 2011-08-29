package com.controlj.addon.colorreport.servlets;

import com.controlj.addon.colorreport.Happy;
import com.controlj.addon.colorreport.HappyEquipment;
import com.controlj.green.addonsupport.web.UITree;
import com.controlj.green.addonsupport.web.Link;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.*;

public class ResultsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


    // build table here in order to return the results of the happy equipment
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        final PrintWriter resp = response.getWriter();
        String location = request.getParameter("location");
        int rawDate = Integer.parseInt(request.getParameter("startdate"));

        //resp.println("Please wait while Trends are being retrieved....</br>");

        try
        {
            Date startDate = determineStartDate(rawDate);
            Date endDate = Calendar.getInstance().getTime();

            resp.println("Results for " + DateFormat.getDateInstance(DateFormat.SHORT).format(startDate) + "   to   " +
                          DateFormat.getDateInstance(DateFormat.SHORT).format(endDate) + ":</br>");
            resp.println("<table>");

            Happy happy = new Happy(request);
            Collection<HappyEquipment> unhappyEquipment = happy.determineUnhappiness("green_trn", startDate, endDate, location);
            if (unhappyEquipment.size() == 0)
            {
                resp.println("</br>Everything is happy :)</br>");
            }
            else
            {
                resp.println("<th>Unhappiness</th>");
                resp.println("<th>Equipment Page</th>");
                for (HappyEquipment eq : unhappyEquipment)
                {
                    resp.print("<tr><td>" + eq.unhappyPercent + "% </td>");
                    resp.print("<td><a target=\"_blank\" href=" + (Link.getURL(request, UITree.GEO, eq.trendPath, "trends",
                            "active", "green_trn", "view")) + ">" + eq.eqDisplayPath);
                    resp.print("</td></tr>");
                }

                resp.print("</table>");
            }
        }
        catch (Exception e)
        {
            resp.println(e.getMessage());
        }

        response.flushBuffer();
    }

    private Date determineStartDate(int numberOfDays)
    {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (numberOfDays == -31)
            c.add(Calendar.MONTH, -1);
        else
            c.add(Calendar.DAY_OF_MONTH, numberOfDays);

        return c.getTime();
    }
}
