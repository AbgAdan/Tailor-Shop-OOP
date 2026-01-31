// com.tailorshop.controller.ClothingTypeController.java
package com.tailorshop.controller;

import com.tailorshop.dao.ClothingTypeDao;
import com.tailorshop.DaoImpl.ClothingTypeDaoImpl;
import com.tailorshop.dao.MeasurementFieldDao;
import com.tailorshop.DaoImpl.MeasurementFieldDaoImpl;
import com.tailorshop.model.ClothingType;

import java.util.List;

public class ClothingTypeController {
    private final ClothingTypeDao dao = new ClothingTypeDaoImpl();
    private final MeasurementFieldDao mfDao = new MeasurementFieldDaoImpl();

    /**
     * Simpan jenis pakaian baharu
     */
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

    /**
     * Dapatkan semua jenis pakaian
     */
    public List<ClothingType> getAllClothingTypes() {
        return dao.getAll();
    }

    /**
     * Dapatkan jenis pakaian mengikut ID
     */
    public ClothingType getClothingTypeById(int id) {
        return dao.findById(id);
    }

    /**
     * Kemas kini nama, jantina dan penerangan
     */
    public void updateTypeNameAndDescription(ClothingType type) {
        // ✅ Guna method update() yang sedia ada
        if (!dao.update(type)) {
            throw new RuntimeException("Gagal mengemaskini jenis pakaian");
        }
    }

    /**
     * Padam jenis pakaian
     */
    public boolean deleteClothingType(int id) {
        return dao.delete(id);
    }

    /**
     * Dapatkan ID kategori mengikut nama
     */
    public int getCategoryIdByName(String categoryName) {
        return dao.getCategoryIdByName(categoryName);
    }

    /**
     * Inisialisasi ukuran asas untuk jenis pakaian baharu
     */
    public void initializeDefaultMeasurements(int typeId, int categoryId) {
        // Auto-salin template dari kategori ke measurement_fields
        mfDao.initializeFromCategoryTemplate(typeId, categoryId, "B0012026"); // Gantikan dengan current user ID sebenar
    }
}