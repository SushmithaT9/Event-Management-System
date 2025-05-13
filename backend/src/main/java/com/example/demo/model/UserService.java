package com.example.demo.model;

import com.example.demo.utils.DBUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private EmailService emailService;

    // Fetch all users for admin dashboard
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        String query = "SELECT id, name, email, role FROM users"; // Do not expose passwords

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Log or handle as necessary
            throw new RuntimeException("Error fetching users from the database", e);
        }

        return users;
    }

    // Register a new user
    public String registerUser(User user) {
        try (Connection conn = DBUtil.getConnection()) {
            String query = "INSERT INTO users (name, email, role) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole());
            stmt.executeUpdate();

            // Send thank you email
            emailService.sendRegistrationEmail(user.getEmail(), user.getName(), "Welcome to the Event Platform");

            return "User registered successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "User registration failed.";
        }
    }

    // Register a user for an event
    public String registerUserForEvent(String email, String eventId) {
        try (Connection conn = DBUtil.getConnection()) {
            // Get user_id by email
            String getUserIdSql = "SELECT id, name FROM users WHERE email = ?";
            PreparedStatement getUserIdStmt = conn.prepareStatement(getUserIdSql);
            getUserIdStmt.setString(1, email);
            ResultSet rs = getUserIdStmt.executeQuery();

            if (!rs.next()) {
                return "User not found";
            }

            int userId = rs.getInt("id");
            String userName = rs.getString("name");

            // Check if user already registered for the event
            String checkSql = "SELECT * FROM registrations WHERE user_id = ? AND event_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            checkStmt.setString(2, eventId);
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                return "User already registered for this event";
            }

            // Register the user for the event
            String insertSql = "INSERT INTO registrations (user_id, event_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertSql);
            stmt.setInt(1, userId);
            stmt.setString(2, eventId);
            stmt.executeUpdate();

            // Retrieve the event title
            String getEventTitleSql = "SELECT title FROM events WHERE id = ?";
            PreparedStatement getEventTitleStmt = conn.prepareStatement(getEventTitleSql);
            getEventTitleStmt.setString(1, eventId);
            ResultSet eventRs = getEventTitleStmt.executeQuery();

            if (!eventRs.next()) {
                return "Event not found";
            }

            String eventTitle = eventRs.getString("title");

            // Send registration confirmation email with event title
            emailService.sendRegistrationEmail(email, userName, eventTitle);

            return "User successfully registered for the event!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error registering for the event";
        }
    }
}
