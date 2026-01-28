// com.tailorshop.model.ClothingCategory.java
package com.tailorshop.model;

public class ClothingCategory {
    private int id;
    private String name;
    private String createdBy;
    private boolean active;

    public ClothingCategory() {}

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}