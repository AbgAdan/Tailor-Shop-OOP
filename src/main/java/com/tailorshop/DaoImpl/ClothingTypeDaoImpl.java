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
        String sql = "INSERT INTO clothing_types (name, gender, category_id, description, created_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getGender());
            
            if (type.getCategoryId() > 0) {
                stmt.setInt(3, type.getCategoryId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.setString(4, type.getDescription());
            stmt.setString(5, type.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<ClothingType> getAll() {
        List<ClothingType> types = new ArrayList<>();
        String sql = "SELECT id, name, gender, category_id, description, created_by FROM clothing_types";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ClothingType type = new ClothingType();
                type.setId(rs.getInt("id"));
                type.setName(rs.getString("name"));
                type.setGender(rs.getString("gender"));
                
                if (rs.getObject("category_id") != null) {
                    type.setCategoryId(rs.getInt("category_id"));
                } else {
                    type.setCategoryId(0);
                }
                
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
        String sql = "SELECT id, name, gender, category_id, description, created_by FROM clothing_types WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ClothingType type = new ClothingType();
                type.setId(rs.getInt("id"));
                type.setName(rs.getString("name"));
                type.setGender(rs.getString("gender"));
                
                if (rs.getObject("category_id") != null) {
                    type.setCategoryId(rs.getInt("category_id"));
                } else {
                    type.setCategoryId(0);
                }
                
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
    public boolean update(ClothingType type) {
        String sql = "UPDATE clothing_types SET name = ?, gender = ?, category_id = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getGender());
            
            if (type.getCategoryId() > 0) {
                stmt.setInt(3, type.getCategoryId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.setString(4, type.getDescription());
            stmt.setInt(5, type.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    @Override
    public int getCategoryIdByName(String categoryName) {
        String sql = "SELECT id FROM clothing_categories WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}