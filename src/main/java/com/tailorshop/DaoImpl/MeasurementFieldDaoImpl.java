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
        String sql = "INSERT INTO measurement_fields (clothing_type_id, field_name, unit, is_required, display_order) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, field.getClothingTypeId());
            stmt.setString(2, field.getFieldName());
            stmt.setString(3, field.getUnit());
            stmt.setBoolean(4, field.isRequired());
            stmt.setInt(5, field.getDisplayOrder());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<MeasurementField> findByClothingTypeId(int clothingTypeId) {
        String sql = "SELECT id, clothing_type_id, field_name, unit, is_required, display_order FROM measurement_fields WHERE clothing_type_id = ? ORDER BY display_order";
        List<MeasurementField> fields = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clothingTypeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MeasurementField field = new MeasurementField();
                field.setId(rs.getInt("id"));
                field.setClothingTypeId(rs.getInt("clothing_type_id"));
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
}