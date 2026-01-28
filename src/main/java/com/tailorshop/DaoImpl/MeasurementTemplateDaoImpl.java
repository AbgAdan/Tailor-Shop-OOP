// com.tailorshop.dao.impl.MeasurementTemplateDaoImpl.java
package com.tailorshop.DaoImpl;

import com.tailorshop.dao.MeasurementTemplateDao;
import com.tailorshop.model.MeasurementTemplate;
import com.tailorshop.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MeasurementTemplateDaoImpl implements MeasurementTemplateDao {

    @Override
    public int save(MeasurementTemplate template) {
        String sql = "INSERT INTO body_measurements (name, created_by, is_active) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, template.getFieldName());
            stmt.setString(2, template.getCreatedBy());
            stmt.setBoolean(3, template.isActive());
            
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
    public List<MeasurementTemplate> getAllActive() {
        String sql = "SELECT id, name, created_by, is_active FROM body_measurements WHERE is_active = TRUE ORDER BY name";
        List<MeasurementTemplate> templates = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MeasurementTemplate template = new MeasurementTemplate();
                template.setId(rs.getInt("id"));
                template.setFieldName(rs.getString("name"));
                template.setCreatedBy(rs.getString("created_by"));
                template.setActive(rs.getBoolean("is_active"));
                templates.add(template);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return templates;
    }

    @Override
    public MeasurementTemplate findById(int id) {
        String sql = "SELECT id, name, created_by, is_active FROM body_measurements WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                MeasurementTemplate template = new MeasurementTemplate();
                template.setId(rs.getInt("id"));
                template.setFieldName(rs.getString("name"));
                template.setCreatedBy(rs.getString("created_by"));
                template.setActive(rs.getBoolean("is_active"));
                return template;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}