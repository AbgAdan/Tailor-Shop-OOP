// com.tailorshop.model.MeasurementField.java
package com.tailorshop.model;

public class MeasurementField {
    private int id;
    private int clothingTypeId;
    private String fieldName;
    private String unit;
    private boolean required;
    private int displayOrder;

    // Constructor kosong
    public MeasurementField() {}

    // Constructor untuk template
    public MeasurementField(String fieldName, String unit, boolean required, int displayOrder) {
        this.fieldName = fieldName;
        this.unit = unit;
        this.required = required;
        this.displayOrder = displayOrder;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClothingTypeId() { return clothingTypeId; }
    public void setClothingTypeId(int clothingTypeId) { this.clothingTypeId = clothingTypeId; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
}