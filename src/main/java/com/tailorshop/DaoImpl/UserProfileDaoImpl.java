// com.tailorshop.dao.impl.UserProfileDaoImpl.java
package com.tailorshop.DaoImpl;

import com.tailorshop.dao.UserProfileDao;
import com.tailorshop.model.UserProfile;
import com.tailorshop.util.DatabaseConnection;

import java.sql.*;

public class UserProfileDaoImpl implements UserProfileDao {

    @Override
    public UserProfile findByUserId(String userId) {
        String sql = "SELECT gender, phone, address FROM user_profiles WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String phone = rs.getString("phone");
                String gender = rs.getString("gender");
                String address = rs.getString("address");
                
                UserProfile profile = new UserProfile(userId, phone);
                profile.setGender(gender);
                profile.setAddress(address);
                return profile;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean save(UserProfile profile) {
        // Gunakan INSERT ... ON DUPLICATE KEY UPDATE
        String sql = "INSERT INTO user_profiles (user_id, gender, phone, address) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE gender = VALUES(gender), phone = VALUES(phone), address = VALUES(address)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, profile.getUserId());
            stmt.setString(2, profile.getGender());
            stmt.setString(3, profile.getPhone());
            stmt.setString(4, profile.getAddress());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isProfileComplete(String userId, String role) {
        UserProfile profile = findByUserId(userId);
        if (profile == null) return false;

        // Semua role: phone wajib
        if (profile.getPhone() == null || profile.getPhone().trim().isEmpty()) {
            return false;
        }

        // Customer: gender & address wajib
        if ("CUSTOMER".equalsIgnoreCase(role)) {
            return profile.getGender() != null && 
                   !profile.getGender().trim().isEmpty() &&
                   profile.getAddress() != null && 
                   !profile.getAddress().trim().isEmpty();
        }

        // Tailor: hanya phone wajib (sudah disemak di atas)
        if ("TAILOR".equalsIgnoreCase(role)) {
            return true;
        }

        // Boss: tiada maklumat tambahan diperlukan
        return true;
    }
}