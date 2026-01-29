// com.tailorshop.dao.MeasurementFieldDao.java
package com.tailorshop.dao;

import com.tailorshop.model.MeasurementField;
import java.util.List;

public interface MeasurementFieldDao {
    boolean save(MeasurementField field);
    List<MeasurementField> findByClothingTypeId(int clothingTypeId);
    boolean delete(int id);
    boolean deleteByClothingTypeId(int clothingTypeId);
    void initializeFromCategoryTemplate(int clothingTypeId, int categoryId, String createdBy);
}