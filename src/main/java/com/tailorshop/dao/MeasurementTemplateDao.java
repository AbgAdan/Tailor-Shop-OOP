// com.tailorshop.dao.MeasurementTemplateDao.java
package com.tailorshop.dao;

import com.tailorshop.model.MeasurementTemplate;
import java.util.List;

public interface MeasurementTemplateDao {
    int save(MeasurementTemplate template);
    List<MeasurementTemplate> getAllActive();
    MeasurementTemplate findById(int id);
}