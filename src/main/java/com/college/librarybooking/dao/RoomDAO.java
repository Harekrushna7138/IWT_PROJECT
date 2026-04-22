package com.college.librarybooking.dao;

import com.college.librarybooking.model.Room;
import com.college.librarybooking.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Room> getAllActiveRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT id, room_number, capacity, location FROM rooms WHERE active = true ORDER BY room_number";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setCapacity(rs.getInt("capacity"));
                room.setLocation(rs.getString("location"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            return rooms;
        }
        return rooms;
    }
}
