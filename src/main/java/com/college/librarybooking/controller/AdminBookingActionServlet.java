package com.college.librarybooking.controller;

import com.college.librarybooking.dao.BookingDAO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/booking-action")
public class AdminBookingActionServlet extends HttpServlet {
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"ADMIN".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int requestId = Integer.parseInt(req.getParameter("requestId"));
        String action = req.getParameter("action");
        String note = req.getParameter("adminNote");

        String status = "approve".equalsIgnoreCase(action) ? "APPROVED" : "REJECTED";
        boolean updated = bookingDAO.updateRequestStatus(requestId, status, note);

        if (updated) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard?success=Request updated");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/admin/dashboard?error=Cannot approve due to room-slot conflict");
    }
}
