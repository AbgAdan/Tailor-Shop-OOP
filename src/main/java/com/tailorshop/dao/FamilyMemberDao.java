// com.tailorshop.dao.FamilyMemberDao.java
package com.tailorshop.dao;

import com.tailorshop.model.FamilyMember;

import java.util.List;

public interface FamilyMemberDao {
    boolean save(FamilyMember member);
    List<FamilyMember> findByCustomerId(String customerId);
    boolean delete(int id);
    boolean hasMainUser(String customerId);
}