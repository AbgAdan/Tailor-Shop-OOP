// com.tailorshop.controller.MeasurementFieldController.java
package com.tailorshop.controller;

import com.tailorshop.dao.MeasurementFieldDao;
import com.tailorshop.DaoImpl.MeasurementFieldDaoImpl;
import com.tailorshop.model.MeasurementField;

import java.util.List;

public class MeasurementFieldController {
    private MeasurementFieldDao dao = new MeasurementFieldDaoImpl();

    public List<MeasurementField> getMeasurementFieldsByClothingTypeId(int clothingTypeId) {
        return dao.findByClothingTypeId(clothingTypeId);
    }

    public void saveMeasurementField(MeasurementField field) {
        if (field.getBodyMeasurementId() <= 0) {
            throw new IllegalArgumentException("Body measurement ID diperlukan");
        }
        if (field.getCreatedBy() == null || field.getCreatedBy().trim().isEmpty()) {
            throw new IllegalArgumentException("Created by diperlukan");
        }
        if (!dao.save(field)) {
            throw new RuntimeException("Gagal menyimpan medan ukuran");
        }
    }

    public void deleteField(int id) {
        if (!dao.delete(id)) {
            throw new RuntimeException("Gagal memadam medan ukuran");
        }
    }

    public void deleteAllFieldsForType(int clothingTypeId) {
        if (!dao.deleteByClothingTypeId(clothingTypeId)) {
            throw new RuntimeException("Gagal memadam semua medan ukuran");
        }
    }
}