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

<jsp:setProperty name="data" property="*"/>


  Created by IntelliJ IDEA.
  User: dreed
  Date: 8/19/11
  Time: 11:39 AM
  To change this template use File | Settings | File Templates.



<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <c:set var="loc" value="${param.location}"/>
    <c:if test="${empty param.location}">
        <c:set var="loc" value="/"/>
    </c:if>
    <c:set var="date" value="${param.date}"/>
    <c:if test="${empty param.date}">
        <c:set var="date" value="MM/DD/YY"/>
    </c:if>
</head>
<body>
this works...
    Data:<br/>
    Start Date: <%= data.getStartdate() %><br/>
    End Date: <%= new Date()%> <br/>
    Location: <%= data.getLocation() %>

<% boolean hasValidDate = false;
    Date startDate = null;
    Date endDate = null;
    String dateString = (String) pageContext.getAttribute("date");
    if (dateString != null) {
        try {
            startDate = DateFormat.getDateInstance(DateFormat.SHORT).parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.DATE, 1);
            endDate = calendar.getTime();
            hasValidDate = true;
        } catch (ParseException e) {
        }
    }
    pageContext.setAttribute("dateClass", hasValidDate ? "" : "error");

    Happy happy = new Happy(request);
    boolean hasValidPath = happy.isPathValid((String) pageContext.getAttribute("loc"));
    pageContext.setAttribute("locClass", hasValidPath ? "" : "error");

    pageContext.setAttribute("run", hasValidDate && hasValidPath);
%>

<div>For all equipment beneath <c:out value="${param.location}"/>, on ${param.date}.</div>
<table>
    &lt;%&ndash;%>
        Collection<HappyEquipment> unhappyEquipment = happy.determineUnhappiness("green_trn", startDate, endDate, request.getParameter("location"));
        for (HappyEquipment eq : unhappyEquipment) {
    %>
    <tr>
        <td><%=eq.unhappyPercent%>%&nbsp;</td>
        <td><a target="_blank"
               href="<%= Link.getURL(request, UITree.GEO, eq.trendPath, "trends", "active", "green_trn", "view")%>"><%=eq.eqDisplayPath%>
        </a></td>
    </tr>
    &lt;%&ndash;%>
        }
    %>
</table>
</body>
</html>