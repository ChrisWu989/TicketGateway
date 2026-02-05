<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<body>
<h2>Login</h2>

<form method="post" action="/login">
    Email: <input type="text" name="username" /><br/>
    Password: <input type="password" name="password" /><br/>
    <button type="submit">Login</button>
</form>

<c:if test="${param.error != null}">
    <p style="color:red">Invalid credentials</p>
</c:if>

<c:if test="${param.logout != null}">
    <p>You have been logged out</p>
</c:if>

</body>
</html>