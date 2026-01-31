// com.tailorshop.model.ClothingType.java
package com.tailorshop.model;

public class ClothingType {
    private int id;
    private String name;
    private String gender; // ✅ JANTINA
    private int categoryId;
    private String description;
    private String createdBy;

    // Constructor kosong
    public ClothingType() {}

    // Constructor penuh
    public ClothingType(int id, String name, String gender, int categoryId, String description, String createdBy) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.categoryId = categoryId;
        this.description = description;
        this.createdBy = createdBy;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; } // ✅
    public void setGender(String gender) { this.gender = gender; } // ✅

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    @Override
    public String toString() {
        return "ClothingType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", categoryId=" + categoryId +
                ", description='" + description + '\'' +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}