// com.tailorshop.dao.impl.FamilyMemberDaoImpl.java
package com.tailorshop.DaoImpl;

import com.tailorshop.dao.FamilyMemberDao;
import com.tailorshop.model.FamilyMember;
import com.tailorshop.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class FamilyMemberDaoImpl implements FamilyMemberDao {

    @Override
    public List<FamilyMember> findByCustomerId(String customerId) {
        String sql = "SELECT id, customer_id, name, gender, birth_date, is_main_user FROM family_members WHERE customer_id = ? ORDER BY is_main_user DESC, name";
        List<FamilyMember> members = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FamilyMember member = new FamilyMember(
                    rs.getInt("id"),
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getDate("birth_date").toString(),
                    rs.getBoolean("is_main_user")
                );
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    @Override
    public boolean save(FamilyMember member) {
        String sql = "INSERT INTO family_members (customer_id, name, gender, birth_date, is_main_user) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, member.getCustomerId());
            stmt.setString(2, member.getName());
            stmt.setString(3, member.getGender());
            stmt.setDate(4, java.sql.Date.valueOf(member.getBirthDate()));
            stmt.setBoolean(5, member.isMainUser());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(FamilyMember member) {
        String sql = "UPDATE family_members SET name = ?, gender = ?, birth_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getGender());
            stmt.setDate(3, java.sql.Date.valueOf(member.getBirthDate()));
            stmt.setInt(4, member.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM family_members WHERE id = ? AND is_main_user = 0";
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
    public boolean hasMainUser(String customerId) {
        String sql = "SELECT COUNT(*) FROM family_members WHERE customer_id = ? AND is_main_user = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public FamilyMember findMainUserByCustomerId(String customerId) {
        String sql = "SELECT id, customer_id, name, gender, birth_date, is_main_user FROM family_members WHERE customer_id = ? AND is_main_user = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new FamilyMember(
                    rs.getInt("id"),
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getDate("birth_date").toString(),
                    rs.getBoolean("is_main_user")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ DAPATKAN SEMUA JENIS PAKAIAN
    @Override
    public List<String> getAllClothingTypeNames() {
        List<String> types = new ArrayList<>();
        String sql = "SELECT name FROM clothing_types ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                types.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    // ✅ DAPATKAN JENIS PAKAIAN MENGIKUT JANTINA
    @Override
    public List<String> getClothingTypeNamesByGender(String memberGender) {
        List<String> types = new ArrayList<>();
        
        if (memberGender == null || memberGender.trim().isEmpty()) {
            // Jika jantina tidak tentu, tunjuk semua
            return getAllClothingTypeNames();
        }
        
        String sql;
        if ("Lelaki".equals(memberGender)) {
            sql = "SELECT name FROM clothing_types WHERE gender IN ('Lelaki', 'Unisex') ORDER BY name";
        } else if ("Perempuan".equals(memberGender)) {
            sql = "SELECT name FROM clothing_types WHERE gender IN ('Perempuan', 'Unisex') ORDER BY name";
        } else {
            // Jika jantina lain, tunjuk semua
            sql = "SELECT name FROM clothing_types ORDER BY name";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                types.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    @Override
    public Map<String, String> getBasicBodyMeasurements(int memberId) {
        Map<String, String> measurements = new LinkedHashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Dapatkan semua ukuran asas dari body_measurements
            String templateSql = "SELECT name FROM body_measurements ORDER BY name";
            PreparedStatement templateStmt = conn.prepareStatement(templateSql);
            ResultSet templateRs = templateStmt.executeQuery();
            
            while (templateRs.next()) {
                // Tunjuk "0" jika tiada nilai
                measurements.put(templateRs.getString("name"), "0");
            }
            
            // Gantikan dengan nilai sebenar jika ada
            String dataSql = "SELECT measurement_data FROM measurements WHERE family_member_id = ? AND clothing_type_id = 0";
            PreparedStatement dataStmt = conn.prepareStatement(dataSql);
            dataStmt.setInt(1, memberId);
            ResultSet dataRs = dataStmt.executeQuery();
            
            if (dataRs.next()) {
                String jsonData = dataRs.getString("measurement_data");
                if (jsonData != null && !jsonData.trim().isEmpty()) {
                    Map<String, String> actualMeasurements = parseJsonToMap(jsonData);
                    for (String key : actualMeasurements.keySet()) {
                        if (measurements.containsKey(key)) {
                            measurements.put(key, actualMeasurements.get(key));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return measurements;
    }

    @Override
    public Map<String, String> getMeasurementsByTemplate(int memberId, int clothingTypeId) {
        Map<String, String> result = new LinkedHashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Dapatkan template dengan JOIN ke body_measurements
            String templateSql = "SELECT bm.name as field_name FROM measurement_fields mf " +
                               "JOIN body_measurements bm ON mf.body_measurement_id = bm.id " +
                               "WHERE mf.clothing_type_id = ? ORDER BY mf.display_order";
            PreparedStatement templateStmt = conn.prepareStatement(templateSql);
            templateStmt.setInt(1, clothingTypeId);
            ResultSet templateRs = templateStmt.executeQuery();
            
            List<String> templateFields = new ArrayList<>();
            while (templateRs.next()) {
                String fieldName = templateRs.getString("field_name");
                templateFields.add(fieldName);
                // Tunjuk "0" jika tiada nilai
                result.put(fieldName, "0");
            }
            
            if (templateFields.isEmpty()) {
                // Tiada template - fallback ke semua ukuran asas
                return getBasicBodyMeasurements(memberId);
            }
            
            // Dapatkan nilai sebenar dari measurements
            String dataSql = "SELECT measurement_data FROM measurements WHERE family_member_id = ? AND clothing_type_id = ?";
            PreparedStatement dataStmt = conn.prepareStatement(dataSql);
            dataStmt.setInt(1, memberId);
            dataStmt.setInt(2, clothingTypeId);
            ResultSet dataRs = dataStmt.executeQuery();
            
            if (dataRs.next()) {
                String jsonData = dataRs.getString("measurement_data");
                if (jsonData != null && !jsonData.trim().isEmpty()) {
                    Map<String, String> actualMeasurements = parseJsonToMap(jsonData);
                    for (String fieldName : templateFields) {
                        if (actualMeasurements.containsKey(fieldName)) {
                            result.put(fieldName, actualMeasurements.get(fieldName));
                        }
                        // Jika tidak ada dalam JSON, kekal "0"
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    @Override
    public int getClothingTypeIdByName(String name) {
        String sql = "SELECT id FROM clothing_types WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean updateMeasurementsByTemplate(int memberId, int clothingTypeId, Map<String, String> measurements) {
        // Semak jika rekod sedia ada
        String checkSql = "SELECT id FROM measurements WHERE family_member_id = ? AND clothing_type_id = ?";
        int measurementId = -1;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, memberId);
            checkStmt.setInt(2, clothingTypeId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                measurementId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Convert Map ke JSON string
        String jsonData = mapToJson(measurements);

        if (measurementId > 0) {
            // Update rekod sedia ada
            String updateSql = "UPDATE measurements SET measurement_data = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, jsonData);
                updateStmt.setInt(2, measurementId);
                return updateStmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // Insert rekod baharu
            String insertSql = "INSERT INTO measurements (family_member_id, clothing_type_id, measurement_data) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, memberId);
                insertStmt.setInt(2, clothingTypeId);
                insertStmt.setString(3, jsonData);
                return insertStmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    // ✅ HELPER METHODS UNTUK JSON (MANUAL)
    private Map<String, String> parseJsonToMap(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty() || !json.startsWith("{")) {
            return map;
        }
        
        String content = json.trim().substring(1, json.length() - 1).trim();
        if (content.isEmpty()) return map;
        
        List<String> pairs = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : content.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (c == ',' && !inQuotes) {
                pairs.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            pairs.add(current.toString().trim());
        }
        
        for (String pair : pairs) {
            if (pair.contains(":")) {
                int colonIndex = pair.indexOf(':');
                String key = pair.substring(0, colonIndex).trim();
                String value = pair.substring(colonIndex + 1).trim();
                
                if (key.startsWith("\"") && key.endsWith("\"")) {
                    key = key.substring(1, key.length() - 1);
                }
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                
                map.put(key, value);
            }
        }
        
        return map;
    }

    private String mapToJson(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"")
                .append(entry.getKey().replace("\"", "\\\""))
                .append("\":\"")
                .append(entry.getValue().replace("\"", "\\\""))
                .append("\"");
            first = false;
        }
        
        json.append("}");
        return json.toString();
    }
}