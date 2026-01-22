// com.tailorshop.controller.ProfileController.java
package com.tailorshop.controller;

import com.tailorshop.dao.UserProfileDao;
import com.tailorshop.DaoImpl.UserProfileDaoImpl;
import com.tailorshop.dao.FamilyMemberDao;
import com.tailorshop.DaoImpl.FamilyMemberDaoImpl;
import com.tailorshop.dao.UserDao;
import com.tailorshop.DaoImpl.UserDaoImpl;
import com.tailorshop.model.UserProfile;
import com.tailorshop.model.FamilyMember;

import java.time.LocalDate;

public class ProfileController {
    private UserProfileDao profileDao = new UserProfileDaoImpl();
    private FamilyMemberDao familyMemberDao = new FamilyMemberDaoImpl();

    /**
     * Simpan profil untuk Customer (dengan tarikh lahir & auto-tambah user utama)
     */
    public void saveProfileWithBirthDate(String userId, String role, String gender, String phone, String address, LocalDate birthDate) {
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
            if (birthDate == null) {
                throw new IllegalArgumentException("Tarikh lahir diperlukan");
            }
        }

        // Simpan ke user_profiles
        UserProfile profile = new UserProfile(userId, phone.trim());
        profile.setGender(gender);
        profile.setAddress(address != null ? address.trim() : null);
        profile.setBirthDate(birthDate);
        if (!profileDao.save(profile)) {
            throw new RuntimeException("Gagal menyimpan profil pengguna");
        }

        // 🔑 Auto-tambah user utama ke family_members
        if ("CUSTOMER".equalsIgnoreCase(role)) {
            if (!familyMemberDao.hasMainUser(userId)) {
                UserDao userDao = new UserDaoImpl();
                String userName = userDao.findNameById(userId);
                if (userName == null || userName.trim().isEmpty()) {
                    throw new RuntimeException("Nama pengguna tidak dijumpai");
                }
                FamilyMember mainUser = new FamilyMember(userId, userName, gender, birthDate, true);
                if (!familyMemberDao.save(mainUser)) {
                    throw new RuntimeException("Gagal menyimpan ahli keluarga utama");
                }
            }
        }
    }

    /**
     * Simpan profil asas (untuk Tailor)
     */
    public void saveProfile(String userId, String role, String gender, String phone, String address) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombor telefon diperlukan");
        }

        UserProfile profile = new UserProfile(userId, phone.trim());
        profile.setGender(gender);
        profile.setAddress(address);
        if (!profileDao.save(profile)) {
            throw new RuntimeException("Gagal menyimpan profil");
        }
    }

    public UserProfile getProfile(String userId) {
        return profileDao.findByUserId(userId);
    }

    public boolean isProfileComplete(String userId, String role) {
        return profileDao.isProfileComplete(userId, role);
    }
}