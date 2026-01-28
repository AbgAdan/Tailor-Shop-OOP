// com.tailorshop.model.MeasurementTemplate.java
package com.tailorshop.model;

public class MeasurementTemplate {
    private int id;
    private String fieldName;
    private String unit;
    private String createdBy;
    private boolean active;

    public MeasurementTemplate() {}

    public MeasurementTemplate(String fieldName, String unit) {
        this.fieldName = fieldName;
        this.unit = unit;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return fieldName + " (" + unit + ")";
    }
}