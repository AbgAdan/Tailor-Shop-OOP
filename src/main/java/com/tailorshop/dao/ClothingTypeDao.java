// com.tailorshop.dao.ClothingTypeDao.java
package com.tailorshop.dao;

import com.tailorshop.model.ClothingType;

import java.util.List;

public interface ClothingTypeDao {
    int save(ClothingType type);
    List<ClothingType> getAll();
    ClothingType findById(int id);
    boolean delete(int id);
}