// com.tailorshop.dao.ClothingTypeDao.java
package com.tailorshop.dao;

import com.tailorshop.model.ClothingType;
import java.util.List;

public interface ClothingTypeDao {
    /**
     * Simpan jenis pakaian baharu
     */
    int save(ClothingType type);

    /**
     * Dapatkan semua jenis pakaian
     */
    List<ClothingType> getAll();

    /**
     * Dapatkan jenis pakaian mengikut ID
     */
    ClothingType findById(int id);

    /**
     * Kemas kini jenis pakaian
     */
    boolean update(ClothingType type);

    /**
     * Padam jenis pakaian
     */
    boolean delete(int id);

    /**
     * Dapatkan ID kategori mengikut nama
     */
    int getCategoryIdByName(String categoryName);
}