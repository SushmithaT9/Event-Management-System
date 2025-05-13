package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.EmailService;
import com.example.demo.model.UserService;
import com.example.demo.utils.DBUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // allow React frontend
public class UserController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;
 // ✅ Get All Users for Admin Dashboard
    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    // Register API
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");
        String role = "user";

        try (Connection conn = DBUtil.getConnection()) {
            String checkEmailSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkEmailSql);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                return ResponseEntity.status(400).body("Email is already registered");
            }

            String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.executeUpdate();

            emailService.sendRegistrationEmail(email, name, "Welcome to Event Platform");

            return ResponseEntity.ok("User registered successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Registration failed");
        }
    }

    // Login API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, String> result = new HashMap<>();
                result.put("email", email);
                result.put("role", rs.getString("role"));
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(401).body("Invalid credentials.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Login failed due to database error.");
        }
    }

    // Event Registration API
    @PostMapping("/registerEvent")
    public ResponseEntity<?> registerEvent(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String eventId = body.get("eventId");

        try (Connection conn = DBUtil.getConnection()) {
            // Get user_id by email
            String getUserIdSql = "SELECT id FROM users WHERE email = ?";
            PreparedStatement getUserIdStmt = conn.prepareStatement(getUserIdSql);
            getUserIdStmt.setString(1, email);
            ResultSet rs = getUserIdStmt.executeQuery();

            if (!rs.next()) {
                return ResponseEntity.status(400).body("User not found");
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
                return ResponseEntity.status(400).body("User already registered for this event");
            }

            // Register user
            String insertSql = "INSERT INTO registrations (user_id, event_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertSql);
            stmt.setInt(1, userId);
            stmt.setString(2, eventId);
            stmt.executeUpdate();

            String getEventTitleSql = "SELECT title FROM events WHERE id = ?";
            PreparedStatement getEventTitleStmt = conn.prepareStatement(getEventTitleSql);
            getEventTitleStmt.setString(1, eventId);
            ResultSet eventRs = getEventTitleStmt.executeQuery();

            if (!eventRs.next()) {
                return ResponseEntity.status(400).body("Event not found");
            }

            String eventTitle = eventRs.getString("title");

            // Send registration confirmation email with event title
            emailService.sendRegistrationEmail(email, userName, eventTitle);

            return ResponseEntity.ok("User registered for the event successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error registering for the event");
        }
    }
   
}
