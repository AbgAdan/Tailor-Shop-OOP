// com.tailorshop.controller.ProfileController.java
package com.tailorshop.controller;

import com.tailorshop.dao.UserProfileDao;
import com.tailorshop.DaoImpl.UserProfileDaoImpl;
import com.tailorshop.model.UserProfile;

public class ProfileController {
    private UserProfileDao profileDao = new UserProfileDaoImpl();

    public UserProfile getProfile(String userId) {
        return profileDao.findByUserId(userId);
    }

    public void saveProfile(String userId, String role, String gender, String phone, String address) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombor telefon diperlukan");
        }

        if ("CUSTOMER".equalsIgnoreCase(role)) {
            if (gender == null || gender.trim().isEmpty()) {
                throw new IllegalArgumentException("Jantina diperlukan");
            }
            if (address == null || address.trim().isEmpty()) {
                throw new IllegalArgumentException("Alamat diperlukan");
            }
        }

        UserProfile profile = new UserProfile(userId, phone.trim());
        profile.setGender(gender);
        profile.setAddress(address != null ? address.trim() : null);

        if (!profileDao.save(profile)) {
            throw new RuntimeException("Gagal menyimpan profil");
        }
    }

    public boolean isProfileComplete(String userId, String role) {
        return profileDao.isProfileComplete(userId, role);
    }
}