<%@ page import="java.util.List" %>
<%@ page import="com.college.librarybooking.model.Room" %>
<%@ page import="com.college.librarybooking.model.BookingRequest" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<div class="container">
    <div class="nav">
        <div class="brand">Library Booking</div>
        <div>
            <span class="small">Welcome, <%= session.getAttribute("name") %> (<%= session.getAttribute("studentId") %>)</span>
            <a class="btn btn-muted" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>

    <% if (request.getParameter("success") != null) { %>
    <div class="alert alert-success"><%= request.getParameter("success") %></div>
    <% } %>

    <% if (request.getParameter("error") != null) { %>
    <div class="alert alert-error"><%= request.getParameter("error") %></div>
    <% } %>

    <div class="grid">
        <div class="card">
            <h3>Request Study Room</h3>
            <form method="post" action="${pageContext.request.contextPath}/booking/create">
                <label>Room Number</label>
                <select name="roomId" required>
                    <option value="">Select a room</option>
                    <% List<Room> rooms = (List<Room>) request.getAttribute("rooms");
                        for (Room room : rooms) { %>
                    <option value="<%= room.getId() %>"><%= room.getRoomNumber() %> (Cap: <%= room.getCapacity() %>)</option>
                    <% } %>
                </select>

                <label>Date</label>
                <input id="bookingDate" type="date" name="bookingDate" required>

                <label>Time Slot</label>
                <select name="timeSlot" required>
                    <option value="">Select slot</option>
                    <option>09:00-11:00</option>
                    <option>11:00-13:00</option>
                    <option>14:00-16:00</option>
                    <option>16:00-18:00</option>
                </select>

                <br><br>
                <button type="submit" class="btn btn-primary">Submit Request</button>
            </form>
        </div>

        <div class="card">
            <h3>Available Rooms</h3>
            <div class="table-wrap">
                <table class="table">
                    <tr><th>Room</th><th>Capacity</th><th>Location</th></tr>
                    <% for (Room room : (List<Room>) request.getAttribute("rooms")) { %>
                    <tr>
                        <td><%= room.getRoomNumber() %></td>
                        <td><%= room.getCapacity() %></td>
                        <td><%= room.getLocation() %></td>
                    </tr>
                    <% } %>
                </table>
            </div>
        </div>
    </div>

    <div class="card" style="margin-top:18px;">
        <h3>My Booking Status</h3>
        <div class="table-wrap">
            <table class="table">
                <tr><th>Request ID</th><th>Room</th><th>Date</th><th>Slot</th><th>Status</th><th>Admin Note</th></tr>
                <% for (BookingRequest b : (List<BookingRequest>) request.getAttribute("bookings")) { %>
                <tr>
                    <td>#<%= b.getId() %></td>
                    <td><%= b.getRoomNumber() %></td>
                    <td><%= b.getBookingDate() %></td>
                    <td><%= b.getTimeSlot() %></td>
                    <td>
                        <span class="badge <%= "APPROVED".equals(b.getStatus()) ? "badge-approved" : ("REJECTED".equals(b.getStatus()) ? "badge-rejected" : "badge-pending") %>">
                            <%= b.getStatus() %>
                        </span>
                    </td>
                    <td><%= b.getAdminNote() == null ? "-" : b.getAdminNote() %></td>
                </tr>
                <% } %>
            </table>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
