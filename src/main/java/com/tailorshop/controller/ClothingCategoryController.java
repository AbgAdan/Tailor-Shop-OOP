// com.tailorshop.controller.ClothingCategoryController.java
package com.tailorshop.controller;

import com.tailorshop.dao.ClothingCategoryDao;
import com.tailorshop.DaoImpl.ClothingCategoryDaoImpl;
import com.tailorshop.model.ClothingCategory;

import java.util.List;

public class ClothingCategoryController {
    private ClothingCategoryDao dao = new ClothingCategoryDaoImpl();

    public List<ClothingCategory> getAllCategories() {
        return dao.getAll();
    }

    public int saveCategory(ClothingCategory category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama kategori diperlukan");
        }
        int id = dao.save(category);
        if (id <= 0) {
            throw new RuntimeException("Gagal menyimpan kategori");
        }
        return id;
    }

    public int getCategoryIdByName(String name) {
        ClothingCategory cat = dao.findByName(name);
        if (cat == null) {
            throw new RuntimeException("Kategori tidak dijumpai: " + name);
        }
        return cat.getId();
    }
}