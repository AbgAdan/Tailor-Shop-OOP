// com.tailorshop.dao.TypeMeasurementDao.java
package com.tailorshop.dao;

import com.tailorshop.model.MeasurementField;
import java.util.List;

public interface TypeMeasurementDao {
    List<MeasurementField> findByClothingTypeId(int clothingTypeId);
    void save(MeasurementField field);
    void linkMeasurementToType(int typeId, int measurementId, boolean required, int order);
    boolean delete(int id);
}