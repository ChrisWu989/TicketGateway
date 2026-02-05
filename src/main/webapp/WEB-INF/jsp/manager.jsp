<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<!DOCTYPE html>
<html>
<head>
    <title>Manager Dashboard</title>
</head>
<body>

<h2>Manager Dashboard</h2>

<p>Welcome,
    <sec:authentication property="principal.username"/>
</p>

<ul>
    <li><a href="/tickets/pending">Review Tickets</a></li>
</ul>

<a href="/logout">Logout</a>

</body>
</html>
