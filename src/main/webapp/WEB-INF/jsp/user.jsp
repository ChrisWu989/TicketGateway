<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Dashboard</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        .dashboard-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            margin: 15px 0;
            background-color: #f9f9f9;
        }
        .dashboard-card:hover {
            background-color: #e9e9e9;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            margin: 10px 5px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .btn-success {
            background-color: #28a745;
        }
        .btn-success:hover {
            background-color: #218838;
        }
        .btn-danger {
            background-color: #dc3545;
        }
        .btn-danger:hover {
            background-color: #c82333;
        }
    </style>
</head>
<body>
    <h1>User Dashboard</h1>
    <p>Welcome, <sec:authentication property="principal.username"/>!</p>
    
    <div class="dashboard-card">
        <h3>My Tickets</h3>
        <p>View and manage your tickets</p>
        <a href="/tickets/my_tickets" class="btn">View My Tickets</a>
    </div>
    
    <div class="dashboard-card">
        <h3>Create New Ticket</h3>
        <p>Submit a new support ticket</p>
        <a href="/tickets/create" class="btn btn-success">Create Ticket</a>
    </div>
    
    <div style="margin-top: 30px;">
        <form action="/logout" method="post" style="display: inline;">
            <button type="submit" class="btn btn-danger">Logout</button>
        </form>
    </div>
</body>
</html>
