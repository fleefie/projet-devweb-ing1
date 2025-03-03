<%@ page import="fr.cytech.projet_devweb_ing1.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>HOME</title>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/login">Login</a>
        <%
            User user = (User) session.getAttribute("user");
            if(user != null) {
        %>
            <div id="user_nav">
                <label>Hello, <%= user.getUsername() %></label>
            </div>

            <% if(user.getUsername().equals("admin")) {  %>
            <a href="/admin">Admin panel</a>
            <% } %>
        <% } %>
    </nav>
</body>

