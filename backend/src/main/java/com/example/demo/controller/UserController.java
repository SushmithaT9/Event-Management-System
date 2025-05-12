// src/main/java/com/example/demo/controller/UserController.java
package com.example.demo.controller;

import com.example.demo.utils.DBUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // allow React frontend
public class UserController {

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");
        String role = "user";

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.executeUpdate();
            return ResponseEntity.ok("User registered successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Registration failed");
        }
    }

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
                return ResponseEntity.status(401).body("Invalid credentials");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Login failed");
        }
    }

    // ✅ New endpoint: Get all registered users (for admin)
    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers() {
    	System.out.println("📌 /api/admin/users endpoint hit!");
        List<Map<String, String>> users = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT id, name, email, role FROM users";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, String> user = new HashMap<>();
                user.put("id", String.valueOf(rs.getLong("id")));
                user.put("name", rs.getString("name"));
                user.put("email", rs.getString("email"));
                user.put("role", rs.getString("role"));
                users.add(user);
            }

            return ResponseEntity.ok(users);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to fetch users");
        }
    }
}
