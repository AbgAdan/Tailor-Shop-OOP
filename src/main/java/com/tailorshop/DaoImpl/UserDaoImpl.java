// com.tailorshop.dao.impl.UserDaoImpl
package com.tailorshop.DaoImpl;

import com.tailorshop.dao.UserDao;
import com.tailorshop.model.User;
import com.tailorshop.util.DatabaseConnection;

import java.sql.*;
import java.time.Year;

public class UserDaoImpl implements UserDao {

    @Override
    public User findByEmailAndPassword(String email, String password) {
        String sql = "SELECT id, name, email, role, registered_by FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        password,
                        rs.getString("role"),
                        rs.getString("registered_by")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT id, name, email, password, role, registered_by FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("registered_by")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean save(User user) {
        // Jana ID jika belum ada
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(generateNextId(user.getRole()));
        }

        String sql = "INSERT INTO users (id, name, email, password, role, registered_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRole());
            stmt.setString(6, user.getRegisteredBy());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean userExistsByNameAndEmail(String name, String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE name = ? AND email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setString(2, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String generateNextId(String role) {
        String prefix = getRolePrefix(role);
        String year = String.valueOf(Year.now().getValue());

        String sql = "SELECT MAX(id) FROM users WHERE id LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, prefix + "%" + year);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String lastId = rs.getString(1);
                if (lastId != null) {
                    // Ekstrak nombor urutan (contoh: C0012026 → 001)
                    String numPart = lastId.substring(1, 4); // ambil 3 digit selepas prefix
                    int nextNum = Integer.parseInt(numPart) + 1;
                    return String.format("%s%03d%s", prefix, nextNum, year);
                }
            }
            // ID pertama untuk tahun ini
            return String.format("%s001%s", prefix, year);
        } catch (Exception e) {
            e.printStackTrace();
            return prefix + "001" + year;
        }
    }
    
    @Override
    public String findNameById(String userId) {
        String sql = "SELECT name FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getRolePrefix(String role) {
        switch (role.toUpperCase()) {
            case "CUSTOMER": return "C";
            case "TAILOR":   return "T";
            case "BOSS":     return "B";
            case "ADMIN":    return "A";
            default: throw new IllegalArgumentException("Role tidak sah: " + role);
        }
    }
}