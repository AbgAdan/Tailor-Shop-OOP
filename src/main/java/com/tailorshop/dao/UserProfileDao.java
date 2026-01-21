// com.tailorshop.dao.UserProfileDao
package com.tailorshop.dao;

import com.tailorshop.model.UserProfile;

public interface UserProfileDao {
    UserProfile findByUserId(String userId);
    boolean save(UserProfile profile);
    boolean isProfileComplete(String userId, String role);
}