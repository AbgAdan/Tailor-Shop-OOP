// com.tailorshop.dao.FamilyMemberDao.java
package com.tailorshop.dao;

import com.tailorshop.model.FamilyMember;
import java.util.List;
import java.util.Map;

public interface FamilyMemberDao {
    // Operasi asas ahli keluarga
    List<FamilyMember> findByCustomerId(String customerId);
    boolean save(FamilyMember member);
    boolean update(FamilyMember member);
    boolean delete(int id);
    boolean hasMainUser(String customerId);
    FamilyMember findMainUserByCustomerId(String customerId);
    
    // Operasi ukuran badan
    List<String> getAllClothingTypeNames();
    List<String> getClothingTypeNamesByGender(String memberGender); // ✅ BARU
    Map<String, String> getBasicBodyMeasurements(int memberId);
    Map<String, String> getMeasurementsByTemplate(int memberId, int clothingTypeId);
    int getClothingTypeIdByName(String name);
    boolean updateMeasurementsByTemplate(int memberId, int clothingTypeId, Map<String, String> measurements);
}