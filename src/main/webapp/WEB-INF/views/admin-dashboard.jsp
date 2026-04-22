<%@ page import="java.util.List" %>
<%@ page import="com.college.librarybooking.model.BookingRequest" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<div class="container">
    <div class="nav">
        <div class="brand">Library Admin Panel</div>
        <div>
            <span class="small">Logged in as <%= session.getAttribute("name") %></span>
            <a class="btn btn-muted" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>

    <% if (request.getParameter("success") != null) { %>
    <div class="alert alert-success"><%= request.getParameter("success") %></div>
    <% } %>

    <% if (request.getParameter("error") != null) { %>
    <div class="alert alert-error"><%= request.getParameter("error") %></div>
    <% } %>

    <div class="grid stats-grid">
        <div class="card stat-card">
            <p class="small">Pending</p>
            <h2><%= request.getAttribute("pendingCount") %></h2>
        </div>
        <div class="card stat-card">
            <p class="small">Approved</p>
            <h2><%= request.getAttribute("approvedCount") %></h2>
        </div>
        <div class="card stat-card">
            <p class="small">Rejected</p>
            <h2><%= request.getAttribute("rejectedCount") %></h2>
        </div>
    </div>

    <div class="card">
        <h3>Pending Booking Queue</h3>
        <div class="table-wrap">
            <table class="table">
                <tr>
                    <th>Req ID</th>
                    <th>Student ID</th>
                    <th>Name</th>
                    <th>Room</th>
                    <th>Date</th>
                    <th>Slot</th>
                    <th>Action</th>
                </tr>
                <% List<BookingRequest> requests = (List<BookingRequest>) request.getAttribute("requests");
                    if (requests.isEmpty()) { %>
                <tr><td colspan="7">No pending requests.</td></tr>
                <% } else {
                    for (BookingRequest r : requests) { %>
                <tr>
                    <td>#<%= r.getId() %></td>
                    <td><%= r.getStudentId() %></td>
                    <td><%= r.getStudentName() %></td>
                    <td><%= r.getRoomNumber() %></td>
                    <td><%= r.getBookingDate() %></td>
                    <td><%= r.getTimeSlot() %></td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/admin/booking-action" style="display:flex; gap:8px; align-items:center;">
                            <input type="hidden" name="requestId" value="<%= r.getId() %>">
                            <input type="text" name="adminNote" placeholder="Note (optional)">
                            <button class="btn btn-primary" name="action" value="approve" type="submit">Approve</button>
                            <button class="btn btn-danger" name="action" value="reject" type="submit">Reject</button>
                        </form>
                    </td>
                </tr>
                <% }} %>
            </table>
        </div>
    </div>

    <div class="card" style="margin-top:18px;">
        <h3>Processed History (Last 24 Hours)</h3>
        <form method="get" action="${pageContext.request.contextPath}/admin/dashboard" class="filter-grid">
            <input type="text" name="student" value="<%= request.getParameter("student") == null ? "" : request.getParameter("student") %>" placeholder="Student ID / Name">
            <input type="text" name="room" value="<%= request.getParameter("room") == null ? "" : request.getParameter("room") %>" placeholder="Room number">
            <input type="date" name="date" value="<%= request.getParameter("date") == null ? "" : request.getParameter("date") %>">
            <select name="status">
                <option value="">All Status</option>
                <option value="APPROVED" <%= "APPROVED".equals(request.getParameter("status")) ? "selected" : "" %>>Approved</option>
                <option value="REJECTED" <%= "REJECTED".equals(request.getParameter("status")) ? "selected" : "" %>>Rejected</option>
            </select>
            <button type="submit" class="btn btn-primary">Filter</button>
            <a class="btn btn-muted" href="${pageContext.request.contextPath}/admin/dashboard">Reset</a>
        </form>

        <div class="table-wrap">
            <table class="table">
                <tr>
                    <th>Req ID</th>
                    <th>Student ID</th>
                    <th>Name</th>
                    <th>Room</th>
                    <th>Date</th>
                    <th>Slot</th>
                    <th>Status</th>
                    <th>Note</th>
                </tr>
                <% List<BookingRequest> historyRequests = (List<BookingRequest>) request.getAttribute("historyRequests");
                    if (historyRequests == null || historyRequests.isEmpty()) { %>
                <tr><td colspan="8">No approved/rejected requests in last 24 hours.</td></tr>
                <% } else {
                    for (BookingRequest h : historyRequests) { %>
                <tr>
                    <td>#<%= h.getId() %></td>
                    <td><%= h.getStudentId() %></td>
                    <td><%= h.getStudentName() %></td>
                    <td><%= h.getRoomNumber() %></td>
                    <td><%= h.getBookingDate() %></td>
                    <td><%= h.getTimeSlot() %></td>
                    <td>
                        <span class="badge <%= "APPROVED".equals(h.getStatus()) ? "badge-approved" : "badge-rejected" %>">
                            <%= h.getStatus() %>
                        </span>
                    </td>
                    <td><%= h.getAdminNote() == null ? "-" : h.getAdminNote() %></td>
                </tr>
                <% }} %>
            </table>
        </div>
    </div>
</div>
</body>
</html>
