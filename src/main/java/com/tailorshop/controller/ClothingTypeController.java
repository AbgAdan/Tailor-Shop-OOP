// com.tailorshop.controller.ClothingTypeController.java
package com.tailorshop.controller;

import com.tailorshop.dao.ClothingTypeDao;
import com.tailorshop.DaoImpl.ClothingTypeDaoImpl;
import com.tailorshop.dao.MeasurementFieldDao;
import com.tailorshop.DaoImpl.MeasurementFieldDaoImpl;
import com.tailorshop.model.ClothingType;

import java.util.List;

public class ClothingTypeController {
    private ClothingTypeDao dao = new ClothingTypeDaoImpl();
    private MeasurementFieldDao mfDao = new MeasurementFieldDaoImpl();

    public int saveClothingType(ClothingType type) {
        if (type.getName() == null || type.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama jenis pakaian diperlukan");
        }
        if (type.getCategoryId() <= 0) {
            throw new IllegalArgumentException("Kategori tidak sah");
        }
        
        int id = dao.save(type);
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

    public void updateTypeNameAndDescription(ClothingType type) {
        dao.updateNameAndDescription(type);
    }

    public void initializeDefaultMeasurements(int typeId, int categoryId) {
        // Auto-salin template dari type_measurements ke measurement_fields
        mfDao.initializeFromCategoryTemplate(typeId, categoryId, "B0012026"); // Gantikan dengan current user ID
    }
}