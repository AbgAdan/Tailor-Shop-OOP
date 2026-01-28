// com.tailorshop.dao.MeasurementDao.java
package com.tailorshop.dao;

import com.tailorshop.model.MeasurementField;

import java.util.Map;

public interface MeasurementDao {
    boolean save(int familyMemberId, int clothingTypeId, Map<String, Object> data);
    Map<String, Object> findByFamilyMemberIdAndType(int familyMemberId, int clothingTypeId);
}