package com.college.librarybooking.controller;

import com.college.librarybooking.dao.BookingDAO;
import com.college.librarybooking.dao.RoomDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {
    private final RoomDAO roomDAO = new RoomDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"STUDENT".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        req.setAttribute("rooms", roomDAO.getAllActiveRooms());
        req.setAttribute("bookings", bookingDAO.getBookingsByUser(userId));
        req.getRequestDispatcher("/WEB-INF/views/student-dashboard.jsp").forward(req, resp);
    }
}
