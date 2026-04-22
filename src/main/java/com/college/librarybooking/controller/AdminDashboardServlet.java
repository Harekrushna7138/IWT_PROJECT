package com.college.librarybooking.controller;

import com.college.librarybooking.dao.BookingDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"ADMIN".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String student = req.getParameter("student");
        String room = req.getParameter("room");
        String date = req.getParameter("date");
        String status = req.getParameter("status");

        req.setAttribute("requests", bookingDAO.getPendingRequestsForAdmin());
        req.setAttribute("historyRequests", bookingDAO.getRecentProcessedRequests(student, room, date, status));
        req.setAttribute("pendingCount", bookingDAO.getRequestCountByStatus("PENDING"));
        req.setAttribute("approvedCount", bookingDAO.getRequestCountByStatus("APPROVED"));
        req.setAttribute("rejectedCount", bookingDAO.getRequestCountByStatus("REJECTED"));
        req.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(req, resp);
    }
}
