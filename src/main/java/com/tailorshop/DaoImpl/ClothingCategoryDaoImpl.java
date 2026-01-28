// com.tailorshop.dao.impl.ClothingCategoryDaoImpl.java
package com.tailorshop.DaoImpl;

import com.tailorshop.dao.ClothingCategoryDao;
import com.tailorshop.model.ClothingCategory;
import com.tailorshop.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClothingCategoryDaoImpl implements ClothingCategoryDao {

    @Override
    public int save(ClothingCategory category) {
        String sql = "INSERT INTO clothing_categories (name, created_by, is_active) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getCreatedBy());
            stmt.setBoolean(3, category.isActive());
            
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
    public List<ClothingCategory> getAll() {
        String sql = "SELECT id, name, created_by, is_active FROM clothing_categories WHERE is_active = TRUE ORDER BY name";
        List<ClothingCategory> categories = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ClothingCategory cat = new ClothingCategory();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                cat.setCreatedBy(rs.getString("created_by"));
                cat.setActive(rs.getBoolean("is_active"));
                categories.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public ClothingCategory findByName(String name) {
        String sql = "SELECT id, name, created_by, is_active FROM clothing_categories WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ClothingCategory cat = new ClothingCategory();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                cat.setCreatedBy(rs.getString("created_by"));
                cat.setActive(rs.getBoolean("is_active"));
                return cat;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}