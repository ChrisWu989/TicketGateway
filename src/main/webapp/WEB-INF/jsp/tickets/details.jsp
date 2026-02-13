<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ticket Details</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 1000px;
            margin: 20px auto;
            padding: 20px;
        }
        .ticket-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            background-color: #f9f9f9;
        }
        .ticket-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .status {
            padding: 8px 16px;
            border-radius: 4px;
            font-weight: bold;
        }
        .status-OPEN { background-color: #ffc107; }
        .status-APPROVED { background-color: #28a745; color: white; }
        .status-REJECTED { background-color: #dc3545; color: white; }
        .status-ASSIGNED { background-color: #17a2b8; color: white; }
        .status-RESOLVED { background-color: #6c757d; color: white; }
        .status-CLOSED { background-color: #343a40; color: white; }
        .status-REOPENED { background-color: #fd7e14; }
        .info-row {
            margin: 10px 0;
        }
        .label {
            font-weight: bold;
            display: inline-block;
            width: 150px;
        }
        .history-section {
            margin-top: 30px;
        }
        .history-item {
            border-left: 3px solid #007bff;
            padding: 10px 15px;
            margin: 10px 0;
            background-color: white;
            border-radius: 4px;
        }
        .history-action {
            font-weight: bold;
            color: #007bff;
        }
        .history-date {
            color: #666;
            font-size: 0.9em;
        }
        .btn {
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 4px;
            color: white;
            display: inline-block;
            margin: 5px;
            border: none;
            cursor: pointer;
        }
        .btn-primary { background-color: #007bff; }
        .btn-success { background-color: #28a745; }
        .btn-danger { background-color: #dc3545; }
        .btn-warning { background-color: #ffc107; color: black; }
    </style>
</head>
<body>
    <div class="ticket-card">
        <div class="ticket-header">
            <h1>Ticket #${ticket.id}: ${ticket.title}</h1>
            <span class="status status-${ticket.status}">${ticket.status}</span>
        </div>
        
        <div class="info-row">
            <span class="label">Category:</span>
            ${ticket.category}
        </div>
        
        <div class="info-row">
            <span class="label">Priority:</span>
            <span class="priority-${ticket.priority}">${ticket.priority}</span>
        </div>
        
        <div class="info-row">
            <span class="label">Created By:</span>
            ${ticket.createdBy.email}
        </div>
        
        <div class="info-row">
            <span class="label">Created Date:</span>
            <fmt:formatDate value="${ticket.creationDate}" pattern="yyyy-MM-dd HH:mm:ss" />
        </div>
        
        <c:if test="${not empty ticket.assignee}">
            <div class="info-row">
                <span class="label">Assigned To:</span>
                ${ticket.assignee.email}
            </div>
        </c:if>
        
        <div class="info-row">
            <span class="label">Description:</span>
        </div>
        <div style="margin-left: 150px; white-space: pre-wrap; background: white; padding: 10px; border-radius: 4px;">
            ${ticket.description}
        </div>
        
        <c:if test="${not empty ticket.fileAttachmentPath}">
            <div class="info-row">
                <span class="label">Attachment:</span>
                <a href="/tickets/download/${ticket.fileAttachmentPath}?originalName=${ticket.originalFileName}"
                   style="color:#007bff;">
                    📎 ${not empty ticket.originalFileName ? ticket.originalFileName : ticket.fileAttachmentPath}
                </a>
            </div>
        </c:if>
    </div>
    
    <div class="history-section">
        <h2>Ticket History</h2>
        <c:choose>
            <c:when test="${empty history}">
                <p>No history available</p>
            </c:when>
            <c:otherwise>
                <c:forEach items="${history}" var="entry">
                    <div class="history-item">
                        <div class="history-action">${entry.action}</div>
                        <div class="history-date">
                            by ${entry.actionBy.email} on 
                            <fmt:formatDate value="${entry.actionDate}" pattern="yyyy-MM-dd HH:mm:ss" />
                        </div>
                        <c:if test="${not empty entry.comments}">
                            <div style="margin-top: 5px; font-style: italic;">
                                "${entry.comments}"
                            </div>
                        </c:if>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    
    <div style="margin-top: 30px;">
        <a href="javascript:history.back()" class="btn btn-primary">Back</a>
    </div>
</body>
</html>