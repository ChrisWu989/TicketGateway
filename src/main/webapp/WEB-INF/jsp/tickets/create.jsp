<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Ticket</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"], textarea, select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        textarea {
            resize: vertical;
            min-height: 100px;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover {
            background-color: #45a049;
        }
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
    <h1>Create New Ticket</h1>
    
    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>
    
    <form action="/tickets/create" method="post" enctype="multipart/form-data">
        <div class="form-group">
            <label for="title">Title *</label>
            <input type="text" id="title" name="title" required>
        </div>
        
        <div class="form-group">
            <label for="description">Description *</label>
            <textarea id="description" name="description" required></textarea>
        </div>
        
        <div class="form-group">
            <label for="category">Category *</label>
            <input type="text" id="category" name="category" required 
                   placeholder="e.g., Hardware, Software, Network">
        </div>
        
        <div class="form-group">
            <label for="priority">Priority *</label>
            <select id="priority" name="priority" required>
                <option value="">-- Select Priority --</option>
                <c:forEach items="${priorities}" var="priority">
                    <option value="${priority}">${priority}</option>
                </c:forEach>
            </select>
        </div>
        
        <div class="form-group">
            <label for="attachment">Attachment <span style="color: #888; font-weight: normal;">(optional - max 10MB)</span></label>
            <input type="file" id="attachment" name="attachment"
                   accept=".pdf,.doc,.docx,.xls,.xlsx,.jpg,.jpeg,.png,.gif,.txt,.zip">
            <small style="color: #888; display: block; margin-top: 4px;">
                Supported: PDF, Word, Excel, Images, Text, ZIP
            </small>
        </div>
        
        <button type="submit">Create Ticket</button>
        <a href="/dashboard"><button type="button">Cancel</button></a>
    </form>
</body>
</html>