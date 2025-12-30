<!-- index.jsp -->

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Redirect to login if not logged in
    if (session.getAttribute("userId") != null) {
        response.sendRedirect("dashboard");
    } else {
        response.sendRedirect("login");
    }
%>