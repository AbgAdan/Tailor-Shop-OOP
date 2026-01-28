// com.tailorshop.controller.MeasurementController.java
package com.tailorshop.controller;

import com.tailorshop.dao.MeasurementDao;
import com.tailorshop.DaoImpl.MeasurementDaoImpl;

import java.util.Map;

public class MeasurementController {
    private MeasurementDao dao = new MeasurementDaoImpl();

    public void saveMeasurement(int familyMemberId, int clothingTypeId, Map<String, Object> data) {
        if (!dao.save(familyMemberId, clothingTypeId, data)) {
            throw new RuntimeException("Gagal menyimpan ukuran");
        }
    }

    public Map<String, Object> getMeasurement(int familyMemberId, int clothingTypeId) {
        return dao.findByFamilyMemberIdAndType(familyMemberId, clothingTypeId);
    }
}