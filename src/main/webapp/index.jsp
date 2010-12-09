<%@ page import="com.controlj.addon.colorreport.Happy" %>
<%@ page import="com.controlj.addon.colorreport.HappyEquipment" %>
<%@ page import="com.controlj.green.addonsupport.web.Link" %>
<%@ page import="com.controlj.green.addonsupport.web.UITree" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Date" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
      <title>Color Report</title>
      <style type="text/css">
          body {font-family:sans-serif; color:black; }
          .label { text-align:right; }
          .error { border:solid red 1px; }
      </style>
  </head>
  <body>
  <c:set var="loc" value="${param.location}"/>
  <c:if test="${empty param.location}">
      <c:set var="loc" value="/"/>
  </c:if>
  <c:set var="date" value="${param.date}"/>
  <c:if test="${empty param.date}">
      <c:set var="date" value="MM/DD/YY"/>
  </c:if>
  <%
      boolean hasValidDate = false;
      Date startDate = null;
      Date endDate = null;
      String dateString = (String)pageContext.getAttribute("date");
      if (dateString != null)
      {
          try
          {
              startDate = DateFormat.getDateInstance(DateFormat.SHORT).parse(dateString);
              Calendar calendar = Calendar.getInstance();
              calendar.setTime(startDate);
              calendar.add(Calendar.DATE, 1);
              endDate = calendar.getTime();
              hasValidDate = true;
          } catch (ParseException e) { }
      }
      pageContext.setAttribute("dateClass", hasValidDate?"":"error");

      Happy happy = new Happy(request);
      boolean hasValidPath = happy.isPathValid((String)pageContext.getAttribute("loc"));
      pageContext.setAttribute("locClass", hasValidPath?"":"error");

      pageContext.setAttribute("run", hasValidDate && hasValidPath);
  %>

  <h1>Unhappy Equipment</h1>
  <form action="index.jsp" method="get">
  <table cellpadding="0" cellspacing="0">
      <tr>
          <td class="label">Date:&nbsp;</td>
          <td><input name="date" class="${dateClass}" type="text" value="${date}"/></td>
      </tr>
      <tr>
          <td class="label">Location:&nbsp;</td>
          <td><input name="location" class="${locClass}" type="text" size="80" value="${loc}"/></td>
      </tr>
      <tr>
          <td colspan="2"><input type="submit" value="Run Report"/></td>
      </tr>
  </table>
      <input type="hidden" name="run" value="true"/>
  </form>
    <c:if test="${run}">
        <div>For all equipment beneath <c:out value="${param.location}"/>, on ${param.date}.</div>
        <table>
         <%
         Collection<HappyEquipment> unhappyEquipment = happy.determineUnhappiness("green_trn", startDate, endDate, request.getParameter("location"));
         for (HappyEquipment eq : unhappyEquipment)
         {
         %>
            <tr>
            <td><%=eq.unhappyPercent%>%&nbsp;</td>
            <td><a target="_blank" href="<%= Link.getURL(request, UITree.GEO, eq.trendPath, "trends", "active", "green_trn", "view")%>"><%=eq.eqDisplayPath%></a></td>
            </tr>
         <%
         }
         %>
         </table>
    </c:if>
  </body>
</html>
