// com.tailorshop.model.ClothingType.java
package com.tailorshop.model;

public class ClothingType {
    private int id;
    private String name;
    private int categoryId; // Rujuk ke clothing_categories.id
    private String description;
    private String createdBy;

    public ClothingType() {}

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}