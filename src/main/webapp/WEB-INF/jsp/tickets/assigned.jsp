<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Assigned Tickets - Admin Dashboard</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }
        th {
            background-color: #dc3545;
            color: white;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .priority-HIGH { color: #dc3545; font-weight: bold; }
        .priority-MEDIUM { color: #ffc107; font-weight: bold; }
        .priority-LOW { color: #28a745; }
        .status {
            padding: 4px 8px;
            border-radius: 4px;
            font-weight: bold;
        }
        .status-ASSIGNED { background-color: #17a2b8; color: white; }
        .btn {
            padding: 6px 12px;
            text-decoration: none;
            border-radius: 4px;
            color: white;
            display: inline-block;
            margin: 2px;
            border: none;
            cursor: pointer;
        }
        .btn-primary { background-color: #007bff; }
        .btn-success { background-color: #28a745; }
        .alert {
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
        }
        .alert-success {
            background-color: #d4edda;
            color: #155724;
        }
        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
        }
    </style>
</head>
<body>
    <h1>My Assigned Tickets</h1>
    
    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>
    
    <a href="/dashboard" class="btn btn-primary">Back to Dashboard</a>
    
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Description</th>
                <th>Category</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Created By</th>
                <th>Created Date</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty tickets}">
                    <tr>
                        <td colspan="9" style="text-align: center;">No tickets assigned to you</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${tickets}" var="ticket">
                        <tr>
                            <td>${ticket.id}</td>
                            <td>${ticket.title}</td>
                            <td>${ticket.description}</td>
                            <td>${ticket.category}</td>
                            <td class="priority-${ticket.priority}">${ticket.priority}</td>
                            <td><span class="status status-${ticket.status}">${ticket.status}</span></td>
                            <td>${ticket.createdBy.email}</td>
                            <td><fmt:formatDate value="${ticket.creationDate}" pattern="yyyy-MM-dd HH:mm" /></td>
                            <td>
                                <a href="/tickets/view/${ticket.id}" class="btn btn-primary">View</a>
                                
                                <c:if test="${ticket.status == 'ASSIGNED'}">
                                    <button onclick="resolveTicket(${ticket.id})" class="btn btn-success">Resolve</button>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
    
    <script>
        function resolveTicket(ticketId) {
            var resolution = prompt("Please provide resolution details:");
            if (resolution) {
                var form = document.createElement('form');
                form.method = 'POST';
                form.action = '/tickets/' + ticketId + '/resolve';
                
                var input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'resolutionDetails';
                input.value = resolution;
                form.appendChild(input);
                
                document.body.appendChild(form);
                form.submit();
            }
        }
    </script>
</body>
</html>
