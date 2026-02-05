<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Dashboard</title>
</head>
<body>

<h2>User Dashboard</h2>

<p>Welcome,
    <sec:authentication property="principal.username"/>
</p>

<ul>
    <li><a href="/tickets/create">Create Ticket</a></li>
    <li><a href="/tickets/my">View My Tickets</a></li>
</ul>

<a href="/logout">Logout</a>

</body>
</html>
