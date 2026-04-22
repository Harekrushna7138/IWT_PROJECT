<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login | Library Room Booking</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<div class="container center-box">
    <div class="card">
        <h2>Library Room Booking</h2>
        <p class="small">Sign in to request a study room.</p>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <% if (request.getParameter("registered") != null) { %>
        <div class="alert alert-success">Registration successful. Please login.</div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <label>Email</label>
            <input type="email" name="email" required>

            <label>Password</label>
            <input type="password" name="password" required>

            <br><br>
            <button class="btn btn-primary" type="submit">Login</button>
            <a class="btn btn-muted" href="${pageContext.request.contextPath}/register">Create account</a>
        </form>
    </div>
</div>
</body>
</html>
