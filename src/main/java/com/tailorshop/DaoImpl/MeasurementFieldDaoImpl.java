// com.tailorshop.dao.impl.MeasurementFieldDaoImpl.java
package com.tailorshop.DaoImpl;

import com.tailorshop.dao.MeasurementFieldDao;
import com.tailorshop.model.MeasurementField;
import com.tailorshop.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MeasurementFieldDaoImpl implements MeasurementFieldDao {

    @Override
    public boolean save(MeasurementField field) {
        String sql = "INSERT INTO measurement_fields (clothing_type_id, body_measurement_id, unit, is_required, display_order, created_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, field.getClothingTypeId());
            stmt.setInt(2, field.getBodyMeasurementId());
            stmt.setString(3, field.getUnit());
            stmt.setBoolean(4, field.isRequired());
            stmt.setInt(5, field.getDisplayOrder()); // ✅
            stmt.setString(6, field.getCreatedBy());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<MeasurementField> findByClothingTypeId(int clothingTypeId) {
        String sql = "SELECT mf.id, mf.clothing_type_id, mf.body_measurement_id, mf.unit, " +
                     "mf.is_required, mf.display_order, mf.created_by, bm.name AS field_name " +
                     "FROM measurement_fields mf " +
                     "JOIN body_measurements bm ON mf.body_measurement_id = bm.id " +
                     "WHERE mf.clothing_type_id = ? " +
                     "ORDER BY mf.display_order"; // ✅ SUSUN IKUT display_order
        List<MeasurementField> fields = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clothingTypeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MeasurementField field = new MeasurementField();
                field.setId(rs.getInt("id"));
                field.setClothingTypeId(rs.getInt("clothing_type_id"));
                field.setBodyMeasurementId(rs.getInt("body_measurement_id"));
                field.setFieldName(rs.getString("field_name"));
                field.setUnit(rs.getString("unit"));
                field.setRequired(rs.getBoolean("is_required"));
                field.setDisplayOrder(rs.getInt("display_order")); // ✅
                field.setCreatedBy(rs.getString("created_by"));
                fields.add(field);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fields;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM measurement_fields WHERE id = ?";
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
    public boolean deleteByClothingTypeId(int clothingTypeId) {
        String sql = "DELETE FROM measurement_fields WHERE clothing_type_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clothingTypeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void initializeFromCategoryTemplate(int clothingTypeId, int categoryId, String createdBy) {
        String sql = "INSERT INTO measurement_fields (clothing_type_id, body_measurement_id, unit, is_required, display_order, created_by) " +
                     "SELECT ?, tm.measurement_id, bm.unit, tm.is_required, tm.display_order, ? " +
                     "FROM type_measurements tm " +
                     "JOIN body_measurements bm ON tm.measurement_id = bm.id " +
                     "WHERE tm.category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clothingTypeId);
            stmt.setString(2, createdBy);
            stmt.setInt(3, categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal salin template kategori", e);
        }
    }
}