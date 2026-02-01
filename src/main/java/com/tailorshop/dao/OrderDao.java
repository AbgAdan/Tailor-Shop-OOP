// com.tailorshop.dao.OrderDao.java
package com.tailorshop.dao;

import com.tailorshop.model.Order;

import java.util.List;
import java.util.Map;

public interface OrderDao {
    // Operasi asas pesanan
    List<Order> getOrdersByCustomerId(String customerId);
    List<Order> getOrdersByTailorId(String tailorId);
    List<Order> getAllOrders();
    boolean saveOrder(Order order);
    boolean updateOrderStatus(String orderId, String status);
    Order findById(String orderId);
    String generateNextOrderId();
    
    // Operasi laporan
    List<Map<String, Object>> getOrdersCountByTailor();
    List<Map<String, Object>> getOrdersCountByClothingType();
    List<Map<String, Object>> getOrdersCountByStatus();
    
    // Operasi tambahan
    List<String> getAvailableTailors();
}