package com.college.librarybooking.controller;

import com.college.librarybooking.dao.BookingDAO;
import com.college.librarybooking.model.BookingRequest;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;

@WebServlet("/booking/create")
public class BookingRequestServlet extends HttpServlet {
    private final BookingDAO bookingDAO = new BookingDAO();
    private static final Set<String> ALLOWED_SLOTS = Set.of("09:00-11:00", "11:00-13:00", "14:00-16:00", "16:00-18:00");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"STUDENT".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int roomId;
        LocalDate bookingDate;
        String timeSlot = req.getParameter("timeSlot");
        try {
            roomId = Integer.parseInt(req.getParameter("roomId"));
            bookingDate = LocalDate.parse(req.getParameter("bookingDate"));
        } catch (NumberFormatException | DateTimeParseException e) {
            resp.sendRedirect(req.getContextPath() + "/student/dashboard?error=Invalid room or date format");
            return;
        }

        if (bookingDate.isBefore(LocalDate.now())) {
            resp.sendRedirect(req.getContextPath() + "/student/dashboard?error=Booking date cannot be in the past");
            return;
        }
        if (!ALLOWED_SLOTS.contains(timeSlot)) {
            resp.sendRedirect(req.getContextPath() + "/student/dashboard?error=Invalid time slot selected");
            return;
        }

        BookingRequest request = new BookingRequest();
        request.setUserId((int) session.getAttribute("userId"));
        request.setRoomId(roomId);
        request.setBookingDate(bookingDate);
        request.setTimeSlot(timeSlot);

        boolean created = bookingDAO.createBookingRequest(request);
        if (created) {
            resp.sendRedirect(req.getContextPath() + "/student/dashboard?success=Booking request submitted");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/student/dashboard?error=Room already allocated or duplicate pending request");
    }
}
