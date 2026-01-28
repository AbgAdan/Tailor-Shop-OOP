// com.tailorshop.dao.ClothingCategoryDao.java
package com.tailorshop.dao;

import com.tailorshop.model.ClothingCategory;
import java.util.List;

public interface ClothingCategoryDao {
    int save(ClothingCategory category);
    List<ClothingCategory> getAll();
    ClothingCategory findByName(String name);
}