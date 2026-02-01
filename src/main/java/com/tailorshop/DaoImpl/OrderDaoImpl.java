// com.tailorshop.dao.impl.OrderDaoImpl.java
package com.tailorshop.DaoImpl;

import com.tailorshop.dao.OrderDao;
import com.tailorshop.model.Order;
import com.tailorshop.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;

public class OrderDaoImpl implements OrderDao {

    @Override
    public List<Order> getOrdersByCustomerId(String customerId) {
        String sql = "SELECT o.*, u.name as customer_name, fm.name as family_member_name, " +
                    "ct.name as clothing_type_name, t.name as tailor_name " +
                    "FROM orders o " +
                    "JOIN users u ON o.customer_id = u.id " +
                    "JOIN family_members fm ON o.family_member_id = fm.id " +
                    "JOIN clothing_types ct ON o.clothing_type_id = ct.id " +
                    "LEFT JOIN users t ON o.tailor_id = t.id " +
                    "WHERE o.customer_id = ? ORDER BY o.created_at DESC";
        return executeQuery(sql, customerId);
    }

    @Override
    public List<Order> getOrdersByTailorId(String tailorId) {
        String sql = "SELECT o.*, u.name as customer_name, fm.name as family_member_name, " +
                    "ct.name as clothing_type_name, t.name as tailor_name " +
                    "FROM orders o " +
                    "JOIN users u ON o.customer_id = u.id " +
                    "JOIN family_members fm ON o.family_member_id = fm.id " +
                    "JOIN clothing_types ct ON o.clothing_type_id = ct.id " +
                    "LEFT JOIN users t ON o.tailor_id = t.id " +
                    "WHERE o.tailor_id = ? ORDER BY o.created_at DESC";
        return executeQuery(sql, tailorId);
    }

    @Override
    public List<Order> getAllOrders() {
        String sql = "SELECT o.*, u.name as customer_name, fm.name as family_member_name, " +
                    "ct.name as clothing_type_name, t.name as tailor_name " +
                    "FROM orders o " +
                    "JOIN users u ON o.customer_id = u.id " +
                    "JOIN family_members fm ON o.family_member_id = fm.id " +
                    "JOIN clothing_types ct ON o.clothing_type_id = ct.id " +
                    "LEFT JOIN users t ON o.tailor_id = t.id " +
                    "ORDER BY o.created_at DESC";
        return executeQuery(sql, (String) null);
    }

    private List<Order> executeQuery(String sql, String parameter) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (parameter != null) {
                stmt.setString(1, parameter);
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getString("id"));
                order.setCustomerId(rs.getString("customer_id"));
                order.setFamilyMemberId(rs.getInt("family_member_id"));
                order.setClothingTypeId(rs.getInt("clothing_type_id"));
                order.setTailorId(rs.getString("tailor_id"));
                order.setOrderDate(rs.getDate("order_date").toLocalDate());
                order.setDueDate(rs.getDate("due_date").toLocalDate());
                order.setStatus(rs.getString("status"));
                order.setNotes(rs.getString("notes"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setFamilyMemberName(rs.getString("family_member_name"));
                order.setClothingTypeName(rs.getString("clothing_type_name"));
                order.setTailorName(rs.getString("tailor_name"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public boolean saveOrder(Order order) {
        String sql = "INSERT INTO orders (id, customer_id, family_member_id, clothing_type_id, " +
                    "tailor_id, order_date, due_date, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, order.getId());
            stmt.setString(2, order.getCustomerId());
            stmt.setInt(3, order.getFamilyMemberId());
            stmt.setInt(4, order.getClothingTypeId());
            stmt.setString(5, order.getTailorId());
            stmt.setDate(6, java.sql.Date.valueOf(order.getOrderDate()));
            stmt.setDate(7, java.sql.Date.valueOf(order.getDueDate()));
            stmt.setString(8, order.getStatus());
            stmt.setString(9, order.getNotes());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateOrderStatus(String orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Order findById(String orderId) {
        String sql = "SELECT o.*, u.name as customer_name, fm.name as family_member_name, " +
                    "ct.name as clothing_type_name, t.name as tailor_name " +
                    "FROM orders o " +
                    "JOIN users u ON o.customer_id = u.id " +
                    "JOIN family_members fm ON o.family_member_id = fm.id " +
                    "JOIN clothing_types ct ON o.clothing_type_id = ct.id " +
                    "LEFT JOIN users t ON o.tailor_id = t.id " +
                    "WHERE o.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Order order = new Order();
                order.setId(rs.getString("id"));
                order.setCustomerId(rs.getString("customer_id"));
                order.setFamilyMemberId(rs.getInt("family_member_id"));
                order.setClothingTypeId(rs.getInt("clothing_type_id"));
                order.setTailorId(rs.getString("tailor_id"));
                order.setOrderDate(rs.getDate("order_date").toLocalDate());
                order.setDueDate(rs.getDate("due_date").toLocalDate());
                order.setStatus(rs.getString("status"));
                order.setNotes(rs.getString("notes"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setFamilyMemberName(rs.getString("family_member_name"));
                order.setClothingTypeName(rs.getString("clothing_type_name"));
                order.setTailorName(rs.getString("tailor_name"));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String generateNextOrderId() {
        String prefix = "ORD";
        String year = String.valueOf(Year.now().getValue());

        String sql = "SELECT MAX(id) FROM orders WHERE id LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prefix + "%" + year);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String lastId = rs.getString(1);
                if (lastId != null) {
                    String numPart = lastId.substring(3, 6);
                    int nextNum = Integer.parseInt(numPart) + 1;
                    return String.format("%s%03d%s", prefix, nextNum, year);
                }
            }
            return String.format("%s001%s", prefix, year);
        } catch (Exception e) {
            e.printStackTrace();
            return prefix + "001" + year;
        }
    }

    // ✅ OPERASI LAPORAN
    @Override
    public List<Map<String, Object>> getOrdersCountByTailor() {
        String sql = "SELECT COALESCE(t.name, 'Belum Diassign') as tailor_name, COUNT(*) as order_count " +
                     "FROM orders o " +
                     "LEFT JOIN users t ON o.tailor_id = t.id " +
                     "GROUP BY t.name " +
                     "ORDER BY order_count DESC";
        return executeReportQuery(sql);
    }

    @Override
    public List<Map<String, Object>> getOrdersCountByClothingType() {
        String sql = "SELECT ct.name as clothing_type, COUNT(*) as order_count " +
                     "FROM orders o " +
                     "JOIN clothing_types ct ON o.clothing_type_id = ct.id " +
                     "GROUP BY ct.name " +
                     "ORDER BY order_count DESC";
        return executeReportQuery(sql);
    }

    @Override
    public List<Map<String, Object>> getOrdersCountByStatus() {
        String sql = "SELECT status, COUNT(*) as order_count " +
                     "FROM orders " +
                     "GROUP BY status " +
                     "ORDER BY FIELD(status, 'Menunggu', 'Dalam Proses', 'Siap', 'Dihantar')";
        return executeReportQuery(sql);
    }

    private List<Map<String, Object>> executeReportQuery(String sql) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // ✅ OPERASI TAMBAHAN
    @Override
    public List<String> getAvailableTailors() {
        List<String> tailors = new ArrayList<>();
        String sql = "SELECT id, name FROM users WHERE role = 'TAILOR' ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tailors.add(rs.getString("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tailors;
    }
}