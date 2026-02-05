<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
</head>
<body>

<h2>Admin Dashboard</h2>

<p>Welcome,
    <sec:authentication property="principal.username"/>
</p>

<ul>
    <li><a href="/tickets/approved">Resolve Tickets</a></li>
</ul>

<a href="/logout">Logout</a>

</body>
</html>

