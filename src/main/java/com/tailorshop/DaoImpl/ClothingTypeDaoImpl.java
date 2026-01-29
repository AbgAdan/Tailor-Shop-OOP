// com.tailorshop.dao.impl.ClothingTypeDaoImpl.java
package com.tailorshop.DaoImpl;

import com.tailorshop.dao.ClothingTypeDao;
import com.tailorshop.model.ClothingType;
import com.tailorshop.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClothingTypeDaoImpl implements ClothingTypeDao {

    @Override
    public int save(ClothingType type) {
        String sql = "INSERT INTO clothing_types (name, category_id, description, created_by) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Untuk jenis pakaian sementara, guna nama kosong
            stmt.setString(1, type.getName() != null ? type.getName() : "");
            stmt.setInt(2, type.getCategoryId());
            stmt.setString(3, type.getDescription());
            stmt.setString(4, type.getCreatedBy());
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<ClothingType> getAll() {
        String sql = "SELECT id, name, category_id, description, created_by FROM clothing_types ORDER BY name";
        List<ClothingType> types = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ClothingType type = new ClothingType();
                type.setId(rs.getInt("id"));
                type.setName(rs.getString("name"));
                type.setCategoryId(rs.getInt("category_id"));
                type.setDescription(rs.getString("description"));
                type.setCreatedBy(rs.getString("created_by"));
                types.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    @Override
    public ClothingType findById(int id) {
        String sql = "SELECT id, name, category_id, description, created_by FROM clothing_types WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ClothingType type = new ClothingType();
                type.setId(rs.getInt("id"));
                type.setName(rs.getString("name"));
                type.setCategoryId(rs.getInt("category_id"));
                type.setDescription(rs.getString("description"));
                type.setCreatedBy(rs.getString("created_by"));
                return type;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateNameAndDescription(ClothingType type) {
        String sql = "UPDATE clothing_types SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getDescription());
            stmt.setInt(3, type.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal mengemaskini nama jenis pakaian", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM clothing_types WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}