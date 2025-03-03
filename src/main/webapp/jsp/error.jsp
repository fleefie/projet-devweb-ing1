<%@ page import="fr.cytech.projet_devweb_ing1.util.JSPUtils" %>
<%@ page import="org.apache.jsp.jsp.error_jsp" %>
<%@ page import="jakarta.servlet.jsp.PageContext" %>
<html>
    <header>
        <title>
            Error <%= JSPUtils.getAttributeSingleKey(pageContext, "error_code").orElse("Unknown") %>
        </title>
    </header>
    <body>
        <h1>We've ran into an error!</h1>
        <h3>Error details:</h3>
        <p>
            <%= JSPUtils.getAttributeSingleKey(pageContext, "error").orElse("Unknown Error") %>
        </p>

        <a href="/">Go Home</a>
    </body>
</html>
