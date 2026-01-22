// com.tailorshop.dao.impl.FamilyMemberDaoImpl.java
package com.tailorshop.DaoImpl;

import com.tailorshop.dao.FamilyMemberDao;
import com.tailorshop.model.FamilyMember;
import com.tailorshop.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FamilyMemberDaoImpl implements FamilyMemberDao {

    @Override
    public boolean save(FamilyMember member) {
        String sql = "INSERT INTO family_members (customer_id, name, gender, birth_date, is_main_user) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, member.getCustomerId());
            stmt.setString(2, member.getName());
            stmt.setString(3, member.getGender());
            stmt.setDate(4, Date.valueOf(member.getBirthDate()));
            stmt.setBoolean(5, member.isMainUser());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<FamilyMember> findByCustomerId(String customerId) {
        String sql = "SELECT id, name, gender, birth_date, is_main_user FROM family_members WHERE customer_id = ? ORDER BY is_main_user DESC, id";
        List<FamilyMember> members = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                FamilyMember member = new FamilyMember(
                    customerId,
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getDate("birth_date").toLocalDate(),
                    rs.getBoolean("is_main_user")
                );
                member.setId(rs.getInt("id"));
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM family_members WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean hasMainUser(String customerId) {
        String sql = "SELECT COUNT(*) FROM family_members WHERE customer_id = ? AND is_main_user = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}