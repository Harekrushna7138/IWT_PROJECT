package com.college.librarybooking.dao;

import com.college.librarybooking.model.BookingRequest;
import com.college.librarybooking.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public boolean createBookingRequest(BookingRequest request) {
        String conflictSql = "SELECT COUNT(*) FROM booking_requests WHERE room_id = ? AND booking_date = ? AND time_slot = ? AND status = 'APPROVED'";
        String duplicatePendingSql = "SELECT COUNT(*) FROM booking_requests WHERE user_id = ? AND room_id = ? AND booking_date = ? AND time_slot = ? AND status = 'PENDING'";
        String insertSql = "INSERT INTO booking_requests (user_id, room_id, booking_date, time_slot, status) VALUES (?, ?, ?, ?, 'PENDING')";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement conflictPs = conn.prepareStatement(conflictSql)) {
                conflictPs.setInt(1, request.getRoomId());
                conflictPs.setDate(2, Date.valueOf(request.getBookingDate()));
                conflictPs.setString(3, request.getTimeSlot());

                try (ResultSet rs = conflictPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }
            try (PreparedStatement duplicatePs = conn.prepareStatement(duplicatePendingSql)) {
                duplicatePs.setInt(1, request.getUserId());
                duplicatePs.setInt(2, request.getRoomId());
                duplicatePs.setDate(3, Date.valueOf(request.getBookingDate()));
                duplicatePs.setString(4, request.getTimeSlot());
                try (ResultSet rs = duplicatePs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }

            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setInt(1, request.getUserId());
                insertPs.setInt(2, request.getRoomId());
                insertPs.setDate(3, Date.valueOf(request.getBookingDate()));
                insertPs.setString(4, request.getTimeSlot());
                return insertPs.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public List<BookingRequest> getBookingsByUser(int userId) {
        List<BookingRequest> bookings = new ArrayList<>();
        String sql = "SELECT br.id, br.booking_date, br.time_slot, br.status, br.admin_note, r.room_number " +
                "FROM booking_requests br JOIN rooms r ON br.room_id = r.id " +
                "WHERE br.user_id = ? ORDER BY br.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BookingRequest booking = new BookingRequest();
                    booking.setId(rs.getInt("id"));
                    booking.setBookingDate(rs.getDate("booking_date").toLocalDate());
                    booking.setTimeSlot(rs.getString("time_slot"));
                    booking.setStatus(rs.getString("status"));
                    booking.setAdminNote(rs.getString("admin_note"));
                    booking.setRoomNumber(rs.getString("room_number"));
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            return bookings;
        }
        return bookings;
    }

    public List<BookingRequest> getPendingRequestsForAdmin() {
        List<BookingRequest> requests = new ArrayList<>();
        String sql = "SELECT br.id, br.user_id, br.room_id, br.booking_date, br.time_slot, br.status, u.student_id, u.name, r.room_number " +
                "FROM booking_requests br " +
                "JOIN users u ON br.user_id = u.id " +
                "JOIN rooms r ON br.room_id = r.id " +
                "WHERE br.status = 'PENDING' ORDER BY br.created_at ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BookingRequest request = new BookingRequest();
                request.setId(rs.getInt("id"));
                request.setUserId(rs.getInt("user_id"));
                request.setRoomId(rs.getInt("room_id"));
                request.setBookingDate(rs.getDate("booking_date").toLocalDate());
                request.setTimeSlot(rs.getString("time_slot"));
                request.setStatus(rs.getString("status"));
                request.setStudentId(rs.getString("student_id"));
                request.setStudentName(rs.getString("name"));
                request.setRoomNumber(rs.getString("room_number"));
                requests.add(request);
            }
        } catch (SQLException e) {
            return requests;
        }

        return requests;
    }

    public int getRequestCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM booking_requests WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            return 0;
        }
        return 0;
    }

    public List<BookingRequest> getRecentProcessedRequests(String studentFilter, String roomFilter, String dateFilter, String statusFilter) {
        List<BookingRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT br.id, br.booking_date, br.time_slot, br.status, br.admin_note, u.student_id, u.name, r.room_number " +
                "FROM booking_requests br " +
                "JOIN users u ON br.user_id = u.id " +
                "JOIN rooms r ON br.room_id = r.id " +
                "WHERE br.status IN ('APPROVED', 'REJECTED') " +
                "AND br.updated_at >= (NOW() - INTERVAL 1 DAY)"
        );

        List<Object> params = new ArrayList<>();
        if (studentFilter != null && !studentFilter.isBlank()) {
            sql.append(" AND (u.student_id LIKE ? OR u.name LIKE ?)");
            String value = "%" + studentFilter.trim() + "%";
            params.add(value);
            params.add(value);
        }
        if (roomFilter != null && !roomFilter.isBlank()) {
            sql.append(" AND r.room_number LIKE ?");
            params.add("%" + roomFilter.trim() + "%");
        }
        if (dateFilter != null && !dateFilter.isBlank()) {
            sql.append(" AND br.booking_date = ?");
            params.add(Date.valueOf(dateFilter));
        }
        if (statusFilter != null && !statusFilter.isBlank()) {
            sql.append(" AND br.status = ?");
            params.add(statusFilter.trim().toUpperCase());
        }
        sql.append(" ORDER BY br.updated_at DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BookingRequest request = new BookingRequest();
                    request.setId(rs.getInt("id"));
                    request.setBookingDate(rs.getDate("booking_date").toLocalDate());
                    request.setTimeSlot(rs.getString("time_slot"));
                    request.setStatus(rs.getString("status"));
                    request.setAdminNote(rs.getString("admin_note"));
                    request.setStudentId(rs.getString("student_id"));
                    request.setStudentName(rs.getString("name"));
                    request.setRoomNumber(rs.getString("room_number"));
                    requests.add(request);
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            return requests;
        }
        return requests;
    }

    public boolean updateRequestStatus(int requestId, String newStatus, String adminNote) {
        String roomSlotSql = "SELECT room_id, booking_date, time_slot FROM booking_requests WHERE id = ?";
        String conflictSql = "SELECT COUNT(*) FROM booking_requests WHERE room_id = ? AND booking_date = ? AND time_slot = ? AND status = 'APPROVED' AND id <> ?";
        String updateSql = "UPDATE booking_requests SET status = ?, admin_note = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            int roomId;
            Date bookingDate;
            String timeSlot;

            try (PreparedStatement roomSlotPs = conn.prepareStatement(roomSlotSql)) {
                roomSlotPs.setInt(1, requestId);
                try (ResultSet rs = roomSlotPs.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                    roomId = rs.getInt("room_id");
                    bookingDate = rs.getDate("booking_date");
                    timeSlot = rs.getString("time_slot");
                }
            }

            if ("APPROVED".equalsIgnoreCase(newStatus)) {
                try (PreparedStatement conflictPs = conn.prepareStatement(conflictSql)) {
                    conflictPs.setInt(1, roomId);
                    conflictPs.setDate(2, bookingDate);
                    conflictPs.setString(3, timeSlot);
                    conflictPs.setInt(4, requestId);

                    try (ResultSet rs = conflictPs.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            return false;
                        }
                    }
                }
            }

            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setString(1, newStatus.toUpperCase());
                updatePs.setString(2, adminNote);
                updatePs.setInt(3, requestId);
                return updatePs.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
