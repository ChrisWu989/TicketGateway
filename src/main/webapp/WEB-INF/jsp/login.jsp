<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketGateway — Sign In</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Lato:wght@300;400;700&family=Merriweather:wght@700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            min-height: 100vh;
            background: #ffffff;
            font-family: 'Lato', sans-serif;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .container {
            width: 100%;
            max-width: 380px;
            padding: 20px;
            animation: fadeUp 0.5s ease both;
        }

        @keyframes fadeUp {
            from { opacity: 0; transform: translateY(16px); }
            to   { opacity: 1; transform: translateY(0); }
        }

        /* ── Logo ── */
        .logo {
            text-align: center;
            margin-bottom: 36px;
        }

        .logo h1 {
            font-family: 'Merriweather', serif;
            font-size: 22px;
            color: #111827;
            letter-spacing: -0.3px;
        }

        .logo p {
            font-size: 13px;
            color: #9ca3af;
            margin-top: 4px;
            font-weight: 300;
        }

        /* ── Alerts ── */
        .alert {
            display: flex;
            align-items: flex-start;
            gap: 10px;
            padding: 11px 14px;
            border-radius: 8px;
            font-size: 13.5px;
            margin-bottom: 20px;
            line-height: 1.4;
        }

        .alert-error {
            background: #fef2f2;
            border: 1px solid #fecaca;
            color: #b91c1c;
        }

        .alert-success {
            background: #f0fdf4;
            border: 1px solid #bbf7d0;
            color: #15803d;
        }

        .alert-icon { flex-shrink: 0; margin-top: 1px; font-size: 14px; }

        /* ── Form ── */
        .form-group {
            margin-bottom: 16px;
        }

        label {
            display: block;
            font-size: 13px;
            font-weight: 700;
            color: #374151;
            margin-bottom: 6px;
            letter-spacing: 0.2px;
        }

        input[type="email"],
        input[type="password"],
        input[type="text"] {
            width: 100%;
            padding: 10px 13px;
            border: 1.5px solid #d1d5db;
            border-radius: 8px;
            font-family: 'Lato', sans-serif;
            font-size: 14px;
            color: #111827;
            background: #fff;
            transition: border-color 0.15s, box-shadow 0.15s;
            outline: none;
        }

        input:focus {
            border-color: #1a56db;
            box-shadow: 0 0 0 3px rgba(26, 86, 219, 0.12);
        }

        input::placeholder { color: #9ca3af; }

        /* ── Password row ── */
        .pw-wrap { position: relative; }
        .pw-toggle {
            position: absolute;
            right: 12px; top: 50%;
            transform: translateY(-50%);
            background: none; border: none;
            cursor: pointer; color: #9ca3af;
            font-size: 13px; padding: 2px;
            transition: color 0.15s;
        }
        .pw-toggle:hover { color: #374151; }

        /* ── Submit ── */
        .btn-submit {
            width: 100%;
            padding: 11px;
            margin-top: 6px;
            background: #1a56db;
            color: #fff;
            font-family: 'Lato', sans-serif;
            font-size: 14.5px;
            font-weight: 700;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            letter-spacing: 0.2px;
            transition: background 0.15s, transform 0.1s;
        }

        .btn-submit:hover  { background: #1648c0; }
        .btn-submit:active { transform: scale(0.99); }
    </style>
</head>
<body>

<div class="container">

    <!-- Logo -->
    <div class="logo">
        <h1>TicketGateway</h1>
		<h5>Support Portal</h5>
    </div>

    <!-- Error alert -->
    <c:if test="${param.error != null}">
        <div class="alert alert-error">
            <span class="alert-icon">✕</span>
            <span>Invalid email or password. Please try again.</span>
        </div>
    </c:if>

    <!-- Logout alert -->
    <c:if test="${param.logout != null}">
        <div class="alert alert-success">
            <span class="alert-icon">✓</span>
            <span>You have been signed out successfully.</span>
        </div>
    </c:if>

    <!-- Login form -->
    <form action="/login" method="post">

        <div class="form-group">
            <label for="username">Email</label>
            <input type="email" id="username" name="username"
                   placeholder="you@company.com"
                   autocomplete="email" required>
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <div class="pw-wrap">
                <input type="password" id="password" name="password"
                       placeholder="••••••••"
                       autocomplete="current-password" required>
                <button type="button" class="pw-toggle" onclick="togglePw()" title="Show/hide password">👁</button>
            </div>
        </div>

        <button type="submit" class="btn-submit">Sign In</button>

    </form>
</div>

<script>
    function togglePw() {
        const pw = document.getElementById('password');
        pw.type = pw.type === 'password' ? 'text' : 'password';
    }
</script>

</body>
</html>