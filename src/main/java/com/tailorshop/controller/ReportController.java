// com.tailorshop.controller.ReportController.java
package com.tailorshop.controller;

import com.tailorshop.dao.OrderDao;
import com.tailorshop.DaoImpl.OrderDaoImpl;

import java.util.*;

public class ReportController {
    private final OrderDao orderDao = new OrderDaoImpl();

    // Laporan mengikut Tailor
    public Map<String, Integer> getOrdersByTailor() {
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Object>> data = orderDao.getOrdersCountByTailor();
        for (Map<String, Object> row : data) {
            String tailorName = (String) row.get("tailor_name");
            if (tailorName == null) tailorName = "Belum Diassign";
            int count = ((Number) row.get("order_count")).intValue();
            result.put(tailorName, count);
        }
        return result;
    }

    // Laporan mengikut Jenis Pakaian
    public Map<String, Integer> getOrdersByClothingType() {
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Object>> data = orderDao.getOrdersCountByClothingType();
        for (Map<String, Object> row : data) {
            String clothingType = (String) row.get("clothing_type");
            int count = ((Number) row.get("order_count")).intValue();
            result.put(clothingType, count);
        }
        return result;
    }

    // Laporan mengikut Status
    public Map<String, Integer> getOrdersByStatus() {
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Object>> data = orderDao.getOrdersCountByStatus();
        for (Map<String, Object> row : data) {
            String status = (String) row.get("status");
            int count = ((Number) row.get("order_count")).intValue();
            result.put(status, count);
        }
        return result;
    }
}