// com.tailorshop.dao.impl.TypeMeasurementDaoImpl.java
package com.tailorshop.DaoImpl;

import com.tailorshop.dao.TypeMeasurementDao;
import com.tailorshop.model.MeasurementField;
import com.tailorshop.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeMeasurementDaoImpl implements TypeMeasurementDao {

    @Override
    public List<MeasurementField> findByClothingTypeId(int clothingTypeId) {
    	String sql = "SELECT tm.id, tm.is_required, tm.display_order, " +
                "bm.name AS field_name, bm.unit " +
                "FROM type_measurements tm " +
                "JOIN body_measurements bm ON tm.measurement_id = bm.id " +
                "WHERE tm.clothing_type_id = ? " +
                "ORDER BY tm.display_order";
        List<MeasurementField> fields = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clothingTypeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MeasurementField field = new MeasurementField();
                field.setId(rs.getInt("id"));
                field.setFieldName(rs.getString("field_name"));
                field.setUnit(rs.getString("unit"));
                field.setRequired(rs.getBoolean("is_required"));
                field.setDisplayOrder(rs.getInt("display_order"));
                fields.add(field);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fields;
    }

    @Override
    public void save(MeasurementField field) {
        // Untuk kesederhanaan, kita anggap field sudah ada dalam body_measurements
        // Jadi kita hanya simpan hubungan ke type_measurements
        String sql = "INSERT INTO type_measurements (clothing_type_id, measurement_id, is_required, display_order) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Anda perlu dapatkan measurement_id dari field_name
            int measurementId = getMeasurementIdByName(field.getFieldName());
            if (measurementId <= 0) {
                throw new RuntimeException("Ukuran tidak dijumpai: " + field.getFieldName());
            }
            
            stmt.setInt(1, field.getClothingTypeId());
            stmt.setInt(2, measurementId);
            stmt.setBoolean(3, field.isRequired());
            stmt.setInt(4, field.getDisplayOrder());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal menyimpan medan ukuran", e);
        }
    }

    @Override
    public void linkMeasurementToType(int typeId, int measurementId, boolean required, int order) {
        String sql = "INSERT INTO type_measurements (clothing_type_id, measurement_id, is_required, display_order) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, typeId);
            stmt.setInt(2, measurementId);
            stmt.setBoolean(3, required);
            stmt.setInt(4, order);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal menghubungkan ukuran", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM type_measurements WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method
    private int getMeasurementIdByName(String fieldName) {
        String sql = "SELECT id FROM body_measurements WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fieldName);
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