// com.tailorshop.dao.BodyMeasurementsDao.java
package com.tailorshop.dao;

import com.tailorshop.model.MeasurementTemplate;
import java.util.List;

public interface BodyMeasurementsDao {
    List<MeasurementTemplate> getAllActive();
    MeasurementTemplate findById(int id);
    int save(MeasurementTemplate template);
}