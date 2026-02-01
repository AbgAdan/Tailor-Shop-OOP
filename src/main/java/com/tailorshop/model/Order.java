// com.tailorshop.model.Order.java
package com.tailorshop.model;

import java.time.LocalDate;

public class Order {
    private String id;
    private String customerId;
    private int familyMemberId;
    private int clothingTypeId;
    private String tailorId;
    private LocalDate orderDate;
    private LocalDate dueDate;
    private String status;
    private String notes;
    
    // Display fields (from JOIN queries)
    private String customerName;
    private String familyMemberName;
    private String clothingTypeName;
    private String tailorName;

    // Constructor kosong
    public Order() {}

    // Constructor penuh
    public Order(String id, String customerId, int familyMemberId, int clothingTypeId,
                String tailorId, LocalDate orderDate, LocalDate dueDate, 
                String status, String notes) {
        this.id = id;
        this.customerId = customerId;
        this.familyMemberId = familyMemberId;
        this.clothingTypeId = clothingTypeId;
        this.tailorId = tailorId;
        this.orderDate = orderDate;
        this.dueDate = dueDate;
        this.status = status;
        this.notes = notes;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public int getFamilyMemberId() { return familyMemberId; }
    public void setFamilyMemberId(int familyMemberId) { this.familyMemberId = familyMemberId; }

    public int getClothingTypeId() { return clothingTypeId; }
    public void setClothingTypeId(int clothingTypeId) { this.clothingTypeId = clothingTypeId; }

    public String getTailorId() { return tailorId; }
    public void setTailorId(String tailorId) { this.tailorId = tailorId; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Display field getters/setters
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getFamilyMemberName() { return familyMemberName; }
    public void setFamilyMemberName(String familyMemberName) { this.familyMemberName = familyMemberName; }

    public String getClothingTypeName() { return clothingTypeName; }
    public void setClothingTypeName(String clothingTypeName) { this.clothingTypeName = clothingTypeName; }

    public String getTailorName() { return tailorName; }
    public void setTailorName(String tailorName) { this.tailorName = tailorName; }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", familyMemberId=" + familyMemberId +
                ", clothingTypeId=" + clothingTypeId +
                ", tailorId='" + tailorId + '\'' +
                ", orderDate=" + orderDate +
                ", dueDate=" + dueDate +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}