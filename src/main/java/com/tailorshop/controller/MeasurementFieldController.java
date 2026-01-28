// com.tailorshop.controller.MeasurementFieldController.java
package com.tailorshop.controller;

import com.tailorshop.dao.TypeMeasurementDao;
import com.tailorshop.DaoImpl.TypeMeasurementDaoImpl;
import com.tailorshop.model.MeasurementField;

import java.util.List;

public class MeasurementFieldController {
    private TypeMeasurementDao dao = new TypeMeasurementDaoImpl();

    /**
     * Dapatkan semua medan ukuran untuk jenis pakaian tertentu
     */
    public List<MeasurementField> getMeasurementFieldsByClothingTypeId(int clothingTypeId) {
        return dao.findByClothingTypeId(clothingTypeId);
    }

    /**
     * Simpan medan ukuran baharu
     */
    public void saveMeasurementField(MeasurementField field) {
        if (field.getFieldName() == null || field.getFieldName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama medan ukuran diperlukan");
        }
        dao.save(field);
    }

    /**
     * Hubungkan ukuran sedia ada ke jenis pakaian
     */
    public void linkMeasurementToType(int typeId, int measurementId, boolean required, int order) {
        dao.linkMeasurementToType(typeId, measurementId, required, order);
    }

    public void deleteField(int id) {
        if (!dao.delete(id)) {
            throw new RuntimeException("Gagal memadam medan ukuran");
        }
    }
}