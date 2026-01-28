// com.tailorshop.controller.ClothingTypeController.java
package com.tailorshop.controller;

import com.tailorshop.dao.ClothingTypeDao;
import com.tailorshop.DaoImpl.ClothingTypeDaoImpl;
import com.tailorshop.model.ClothingType;
import com.tailorshop.model.MeasurementTemplate; // ← TAMBAH INI

import java.util.List;

public class ClothingTypeController {
    private ClothingTypeDao dao = new ClothingTypeDaoImpl();

    public int saveClothingType(ClothingType type) {
        if (type.getName() == null || type.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama jenis pakaian diperlukan");
        }
        if (type.getCategoryId() <= 0) {
            throw new IllegalArgumentException("Kategori tidak sah");
        }
        
        int id = dao.save(type); // ← Sekarang tiada ralat
        if (id <= 0) {
            throw new RuntimeException("Gagal menyimpan jenis pakaian");
        }
        return id;
    }

    public List<ClothingType> getAllClothingTypes() {
        return dao.getAll();
    }

    public ClothingType getClothingTypeById(int id) {
        return dao.findById(id);
    }

    public void initializeDefaultMeasurements(int typeId, int categoryId) {
        MeasurementTemplateController templateController = new MeasurementTemplateController();
        List<MeasurementTemplate> templates = templateController.getTemplatesByCategory(categoryId);
        
        MeasurementFieldController mfController = new MeasurementFieldController();
        for (int i = 0; i < templates.size(); i++) {
            mfController.linkMeasurementToType(typeId, templates.get(i).getId(), true, i + 1);
        }
    }
}