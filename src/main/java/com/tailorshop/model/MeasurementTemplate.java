// com.tailorshop.model.MeasurementTemplate.java
package com.tailorshop.model;

public class MeasurementTemplate {
    private int id;
    private String fieldName; // ← mesti diisi dari body_measurements.name
    private String unit;      // ← mesti diisi dari body_measurements.unit

    public MeasurementTemplate() {}

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    @Override
    public String toString() {
        return fieldName + " (" + unit + ")";
    }
}