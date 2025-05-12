package com.example.demo.model;

import com.example.demo.model.User;
import com.example.demo.utils.DBUtil;

import java.sql.*;
import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String query = "SELECT * FROM users";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
                );
                users.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }
}
