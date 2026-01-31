// com.tailorshop.dao.impl.BodyMeasurementsDaoImpl.java
package com.tailorshop.DaoImpl;

import com.tailorshop.dao.BodyMeasurementsDao;
import com.tailorshop.model.MeasurementTemplate;
import com.tailorshop.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BodyMeasurementsDaoImpl implements BodyMeasurementsDao {

    @Override
    public List<MeasurementTemplate> getAllActive() {
        String sql = "SELECT id, name, unit FROM body_measurements WHERE is_active = 1 ORDER BY name";
        List<MeasurementTemplate> templates = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                MeasurementTemplate template = new MeasurementTemplate();
                template.setId(rs.getInt("id"));
                template.setFieldName(rs.getString("name")); // ✅ DARI 'name'
                template.setUnit(rs.getString("unit"));     // ✅ DARI 'unit'
                templates.add(template);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal memuatkan senarai ukuran aktif", e);
        }
        
        return templates;
    }

    @Override
    public MeasurementTemplate findById(int id) {
        String sql = "SELECT id, name, unit FROM body_measurements WHERE id = ? AND is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                MeasurementTemplate template = new MeasurementTemplate();
                template.setId(rs.getInt("id"));
                template.setFieldName(rs.getString("name"));
                template.setUnit(rs.getString("unit"));
                return template;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int save(MeasurementTemplate template) {
        String sql = "INSERT INTO body_measurements (name, unit, created_by) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, template.getFieldName());
            stmt.setString(2, template.getUnit());
            stmt.setString(3, "B0012026"); // Gantikan dengan current user ID
            
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal menyimpan ukuran baharu", e);
        }
        return -1;
    }
}