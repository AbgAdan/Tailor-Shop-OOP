// com.tailorshop.controller.FamilyMemberController.java
package com.tailorshop.controller;

import com.tailorshop.dao.FamilyMemberDao;
import com.tailorshop.DaoImpl.FamilyMemberDaoImpl;
import com.tailorshop.model.FamilyMember;

import java.time.LocalDate;
import java.util.List;

public class FamilyMemberController {
    private FamilyMemberDao dao = new FamilyMemberDaoImpl();

    public void addFamilyMember(String customerId, String name, String gender, LocalDate birthDate, boolean isMainUser) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama diperlukan");
        }
        FamilyMember member = new FamilyMember(customerId, name, gender, birthDate, isMainUser);
        if (!dao.save(member)) {
            throw new RuntimeException("Gagal menyimpan ahli keluarga");
        }
    }

    public List<FamilyMember> getFamilyMembers(String customerId) {
        return dao.findByCustomerId(customerId);
    }

    public void deleteFamilyMember(int id) {
        if (!dao.delete(id)) {
            throw new RuntimeException("Gagal memadam ahli keluarga");
        }
    }

    public boolean hasMainUser(String customerId) {
        return dao.hasMainUser(customerId);
    }
}