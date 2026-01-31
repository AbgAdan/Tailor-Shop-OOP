// com.tailorshop.controller.MeasurementTemplateController.java
package com.tailorshop.controller;

import com.tailorshop.model.MeasurementTemplate;
import com.tailorshop.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MeasurementTemplateController {

    /**
     * Dapatkan semua ukuran aktif dari body_measurements
     */
    public List<MeasurementTemplate> getAllTemplates() {
        String sql = "SELECT id, name, unit FROM body_measurements WHERE is_active = 1 ORDER BY name";
        List<MeasurementTemplate> templates = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                MeasurementTemplate template = new MeasurementTemplate();
                template.setId(rs.getInt("id"));
                template.setFieldName(rs.getString("name")); // ✅ GUNA 'name'
                template.setUnit(rs.getString("unit"));     // ✅ GUNA 'unit'
                templates.add(template);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal memuatkan senarai ukuran", e);
        }
        
        return templates;
    }
}