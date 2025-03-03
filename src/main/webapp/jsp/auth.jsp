<%@ page import="fr.cytech.projet_devweb_ing1.util.JSPUtils" %>
<%@ page import="fr.cytech.projet_devweb_ing1.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>AUTHENTICATION</title>
</head>
<body>
<nav>
    <%
        User user = (User) session.getAttribute("user");
        if(user != null) {
    %>
        <div id="user_nav">
            <label>USER: <%= user.getUsername() %></label>
        </div>
    <% } %>
</nav>
<div id="main">
    <div id="forms">
        <form action="${pageContext.request.contextPath}/auth/endpoint/login" method="post">
            <label class="form_title">Login</label>
            <fieldset>
                <label for="loginUsername">Username:</label>
                <div>
                    <input class="<%=JSPUtils.errorInputClass(pageContext, "error_loginUsername", "error_login")%>"
                           type="text" id="loginUsername" name="loginUsername">
                    <% JSPUtils.errorSpan(pageContext, "error_loginUsername", "error_login"); %>
                </div>
                <label for="loginPassword">Password:</label>
                <div>
                    <input class="<%= JSPUtils.errorInputClass(pageContext, "error_loginPassword", "error_login")%>"
                           type="password" id="loginPassword" name="loginPassword">
                    <% JSPUtils.errorSpan(pageContext, "error_loginPassword", "error_login"); %>
                </div>
            </fieldset>
            <input type="submit" value="Login">
        </form>
        <form action="${pageContext.request.contextPath}/auth/endpoint/register" method="post">
            <label class="form_title">Register</label>
            <fieldset>
                <label for="registerUsername">Username:</label>
                <input class="<%=JSPUtils.errorInputClass(pageContext, "error_registerUsername")%>" type="text"
                       id="registerUsername" name="registerUsername">
                <% JSPUtils.errorSpan(pageContext, "error_registerUsername"); %>
                <label for="registerPassword">Password:</label>
                <input class="<%= JSPUtils.errorInputClass(pageContext, "error_registerPassword") %>" type="password"
                       id="registerPassword" name="registerPassword">
                <% JSPUtils.errorSpan(pageContext, "error_registerPassword"); %>
                <label for="registerPasswordConfirm">Confirm password:</label>
                <input class="<%= JSPUtils.errorInputClass(pageContext, "error_registerPassword") %>" type="password"
                       id="registerPasswordConfirm" name="registerPasswordConfirm">
                <% JSPUtils.errorSpan(pageContext, "error_registerPassword"); %>
            </fieldset>
            <input type="submit" value="Register">
        </form>
    </div>
</div>
</body>
</html>
