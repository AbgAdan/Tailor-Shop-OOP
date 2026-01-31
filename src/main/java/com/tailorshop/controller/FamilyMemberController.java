// com.tailorshop.controller.FamilyMemberController.java
package com.tailorshop.controller;

import com.tailorshop.dao.FamilyMemberDao;
import com.tailorshop.DaoImpl.FamilyMemberDaoImpl;
import com.tailorshop.model.FamilyMember;

import java.util.List;
import java.util.Map;

public class FamilyMemberController {
    private final FamilyMemberDao dao = new FamilyMemberDaoImpl();

    // Operasi asas ahli keluarga
    public List<FamilyMember> getFamilyMembers(String customerId) {
        return dao.findByCustomerId(customerId);
    }

    public FamilyMember getMainUser(String customerId) {
        return dao.findMainUserByCustomerId(customerId);
    }

    public boolean addFamilyMember(FamilyMember member) {
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama diperlukan");
        }
        if (member.getBirthDate() == null || member.getBirthDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Tarikh lahir diperlukan");
        }
        return dao.save(member);
    }

    public boolean removeFamilyMember(int id) {
        return dao.delete(id);
    }

    public boolean updateFamilyMember(FamilyMember member) {
        return dao.update(member);
    }

    // Operasi ukuran badan
    public List<String> getAllClothingTypeNames() {
        return dao.getAllClothingTypeNames();
    }

    // ✅ DAPATKAN JENIS PAKAIAN MENGIKUT JANTINA AHLI KELUARGA
    public List<String> getClothingTypeNamesByGender(String memberGender) {
        return dao.getClothingTypeNamesByGender(memberGender);
    }

    public Map<String, String> getBasicBodyMeasurements(int memberId) {
        return dao.getBasicBodyMeasurements(memberId);
    }

    public Map<String, String> getMeasurementsByTemplate(int memberId, int clothingTypeId) {
        return dao.getMeasurementsByTemplate(memberId, clothingTypeId);
    }

    public int getClothingTypeIdByName(String name) {
        return dao.getClothingTypeIdByName(name);
    }

    public boolean updateMeasurementsByTemplate(int memberId, int clothingTypeId, Map<String, String> measurements) {
        return dao.updateMeasurementsByTemplate(memberId, clothingTypeId, measurements);
    }

    // ✅ METHOD BARU: KEWENANGAN TAILOR
    public boolean grantTailorAccess(int familyMemberId, String tailorId) {
        return dao.grantTailorAccess(familyMemberId, tailorId);
    }

    public boolean revokeTailorAccess(int familyMemberId) {
        return dao.revokeTailorAccess(familyMemberId);
    }

    public boolean isTailorAuthorized(int familyMemberId, String tailorId) {
        return dao.isTailorAuthorized(familyMemberId, tailorId);
    }
}