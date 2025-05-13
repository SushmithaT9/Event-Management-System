package com.example.demo.model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.example.demo.utils.DBUtil;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
@Service
public class EventService {

    // Validate event data
    public boolean validateEvent(Event event) {
        return event.getTitle() != null && !event.getTitle().isEmpty() &&
               event.getDate() != null && !event.getDate().isEmpty() &&
               event.getTime() != null && !event.getTime().isEmpty();
    }

    // Save event to the database
    public boolean saveEvent(Event event) {
        // Check if the event data is valid
        if (!validateEvent(event)) {
            System.out.println("Invalid event data");
            return false;
        }

        try (Connection conn = DBUtil.getConnection()) {
            if (conn != null) {
                System.out.println("Connection established successfully.");
            } else {
                System.out.println("Failed to establish connection.");
                return false;  // Exit early if the connection fails
            }

            // Proceed with saving the event if the connection is successful
            String sql = "INSERT INTO events (title, date, time, location, description) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDate());
            stmt.setString(3, event.getTime());
            stmt.setString(4, event.getLocation());
            stmt.setString(5, event.getDescription());

            System.out.println("PreparedStatement created: " + stmt);  // Log PreparedStatement for debugging

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Event saved successfully with title: " + event.getTitle());
                return true;
            } else {
                System.out.println("No rows affected, event was not saved.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error saving event: " + event.getTitle());
            e.printStackTrace();  // Print full stack trace for debugging
            return false;
        }
    }

    // Get all events from the database
 // Get all events from the database
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM events";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = new Event();
                event.setTitle(rs.getString("title"));
                event.setDate(rs.getString("date"));
                event.setTime(rs.getString("time"));
                event.setLocation(rs.getString("location"));
                event.setDescription(rs.getString("description"));
                event.setStatus(rs.getString("status")); // ✅ Add this line
                events.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
 // Approve event by updating status in DB
    public boolean approveEvent(String title) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "UPDATE events SET status = 'approved' WHERE title = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error approving event: " + title);
            e.printStackTrace(); // Log the stack trace
            return false;
        }
    }

    public boolean rejectEvent(String title) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "DELETE FROM events WHERE title = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error rejecting event: " + title);
            e.printStackTrace(); // Log the stack trace
            return false;
        }
    }
    
   
    public boolean deleteEventByTitle(String title) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "DELETE FROM events WHERE title = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting event: " + title);
            e.printStackTrace();
            return false;
        }
    }



}
