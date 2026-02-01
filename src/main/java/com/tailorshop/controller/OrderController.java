// com.tailorshop.controller.OrderController.java
package com.tailorshop.controller;

import com.tailorshop.dao.OrderDao;
import com.tailorshop.DaoImpl.OrderDaoImpl;
import com.tailorshop.model.Order;

import java.time.LocalDate;
import java.util.List;

public class OrderController {
    private final OrderDao dao = new OrderDaoImpl();

    /**
     * Buat pesanan baru tanpa tailor yang ditentukan
     */
    public void createOrder(String customerId, int familyMemberId, int clothingTypeId, 
                          LocalDate dueDate, String notes) {
        validateInput(customerId, familyMemberId, clothingTypeId, dueDate);
        
        Order order = new Order();
        order.setId(dao.generateNextOrderId());
        order.setCustomerId(customerId);
        order.setFamilyMemberId(familyMemberId);
        order.setClothingTypeId(clothingTypeId);
        order.setOrderDate(LocalDate.now());
        order.setDueDate(dueDate);
        order.setStatus("Menunggu");
        order.setNotes(notes != null ? notes.trim() : "");
        
        if (!dao.saveOrder(order)) {
            throw new RuntimeException("Gagal menyimpan pesanan");
        }
    }

    /**
     * Buat pesanan baru dengan tailor yang ditentukan
     */
    public void createOrderWithTailor(String customerId, int familyMemberId, int clothingTypeId, 
                                    String tailorId, LocalDate dueDate, String notes) {
        validateInput(customerId, familyMemberId, clothingTypeId, dueDate);
        
        Order order = new Order();
        order.setId(dao.generateNextOrderId());
        order.setCustomerId(customerId);
        order.setFamilyMemberId(familyMemberId);
        order.setClothingTypeId(clothingTypeId);
        order.setTailorId(tailorId);
        order.setOrderDate(LocalDate.now());
        order.setDueDate(dueDate);
        order.setStatus("Menunggu");
        order.setNotes(notes != null ? notes.trim() : "");
        
        if (!dao.saveOrder(order)) {
            throw new RuntimeException("Gagal menyimpan pesanan");
        }
    }

    /**
     * Dapatkan semua pesanan untuk customer tertentu
     */
    public List<Order> getCustomerOrders(String customerId) {
        return dao.getOrdersByCustomerId(customerId);
    }

    /**
     * Dapatkan semua pesanan untuk tailor tertentu
     */
    public List<Order> getTailorOrders(String tailorId) {
        return dao.getOrdersByTailorId(tailorId);
    }

    /**
     * Dapatkan semua pesanan (untuk Boss)
     */
    public List<Order> getAllOrders() {
        return dao.getAllOrders();
    }

    /**
     * Kemaskini status pesanan
     */
    public boolean updateOrderStatus(String orderId, String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Status tidak sah: " + status);
        }
        return dao.updateOrderStatus(orderId, status);
    }

    /**
     * Dapatkan pesanan mengikut ID
     */
    public Order getOrderById(String orderId) {
        return dao.findById(orderId);
    }

    /**
     * Dapatkan senarai tailor yang tersedia
     */
    public List<String> getAvailableTailors() {
        return dao.getAvailableTailors();
    }

    /**
     * Validasi input asas
     */
    private void validateInput(String customerId, int familyMemberId, int clothingTypeId, LocalDate dueDate) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID pelanggan diperlukan");
        }
        if (familyMemberId <= 0) {
            throw new IllegalArgumentException("ID ahli keluarga tidak sah");
        }
        if (clothingTypeId <= 0) {
            throw new IllegalArgumentException("ID jenis pakaian tidak sah");
        }
        if (dueDate == null || dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tarikh siap mesti hari ini atau masa hadapan");
        }
    }

    /**
     * Semak jika status adalah sah
     */
    private boolean isValidStatus(String status) {
        return "Menunggu".equals(status) || 
               "Dalam Proses".equals(status) || 
               "Siap".equals(status) || 
               "Dihantar".equals(status);
    }
}