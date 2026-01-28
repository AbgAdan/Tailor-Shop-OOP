// com.tailorshop.dao.impl.MeasurementDaoImpl.java
package com.tailorshop.DaoImpl;

import com.tailorshop.dao.MeasurementDao;
import com.tailorshop.util.DatabaseConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MeasurementDaoImpl implements MeasurementDao {

    @Override
    public boolean save(int familyMemberId, int clothingTypeId, Map<String, Object> data) {
        // Tukar Map kepada JSON string
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) json.append(",");
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        json.append("}");

        String sql = "INSERT INTO measurements (family_member_id, clothing_type_id, measurement_data) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE measurement_data = VALUES(measurement_data)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, familyMemberId);
            stmt.setInt(2, clothingTypeId);
            stmt.setString(3, json.toString());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, Object> findByFamilyMemberIdAndType(int familyMemberId, int clothingTypeId) {
        String sql = "SELECT measurement_data FROM measurements WHERE family_member_id = ? AND clothing_type_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, familyMemberId);
            stmt.setInt(2, clothingTypeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String jsonData = rs.getString("measurement_data");
                return parseJsonToMap(jsonData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    private Map<String, Object> parseJsonToMap(String json) {
        Map<String, Object> map = new HashMap<>();
        if (json == null || json.isEmpty()) return map;
        
        // Remove braces
        json = json.trim().replaceAll("^\\{", "").replaceAll("\\}$", "");
        if (json.isEmpty()) return map;
        
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("^\"|\"$", "");
                String value = keyValue[1].trim().replaceAll("^\"|\"$", "");
                map.put(key, value);
            }
        }
        return map;
    }
}