<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register | Library Room Booking</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<div class="container center-box">
    <div class="card">
        <h2>Create Student Account</h2>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/register">
            <label>Student ID</label>
            <input type="text" name="studentId" pattern="[A-Za-z0-9_-]{4,30}" title="4-30 chars: letters, numbers, _ or -" required>

            <label>Full Name</label>
            <input type="text" name="name" required>

            <label>Email</label>
            <input type="email" name="email" required>

            <label>Password</label>
            <input type="password" name="password" minlength="6" required>

            <br><br>
            <button class="btn btn-primary" type="submit">Register</button>
            <a class="btn btn-muted" href="${pageContext.request.contextPath}/login">Back to login</a>
        </form>
    </div>
</div>
</body>
</html>
