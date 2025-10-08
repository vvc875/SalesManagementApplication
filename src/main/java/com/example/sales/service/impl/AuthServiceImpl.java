package com.example.sales.service.impl;

import com.example.sales.entity.User;
import com.example.sales.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //Mapper để chuyển từ ResultSet → User object
    private static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setRole(rs.getString("role"));
            return u;
        }
    }

    @Override
    public boolean login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        List<User> result = jdbcTemplate.query(sql, new Object[]{username, password}, new UserMapper());
        return !result.isEmpty(); // có người dùng hợp lệ
    }

    @Override
    public boolean register(User user) {
        // Kiểm tra username đã tồn tại chưa
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, user.getUsername());
        if (count != null && count > 0) {
            return false; // username bị trùng
        }

        // Thêm user mới
        String insertSql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";
        int rows = jdbcTemplate.update(insertSql, user.getUsername(), user.getPassword(), user.getRole());
        return rows > 0;
    }

    @Override
    public String getRoleByUsername(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, username);
        } catch (Exception e) {
            return null;
        }
    }
}
