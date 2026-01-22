// com.tailorshop.controller.ProfileController.java
package com.tailorshop.controller;

import com.tailorshop.dao.UserProfileDao;
import com.tailorshop.DaoImpl.UserProfileDaoImpl;
import com.tailorshop.dao.FamilyMemberDao;
import com.tailorshop.DaoImpl.FamilyMemberDaoImpl;
import com.tailorshop.model.UserProfile;
import com.tailorshop.model.FamilyMember;

import java.time.LocalDate;

public class ProfileController {
    private UserProfileDao profileDao = new UserProfileDaoImpl();
    private FamilyMemberDao familyMemberDao = new FamilyMemberDaoImpl();

    /**
     * Simpan profil untuk Customer (dengan tarikh lahir)
     */
    public void saveProfileWithBirthDate(String userId, String role, String gender, String phone, String address, LocalDate birthDate) {
        // Validasi
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
        profile.setBirthDate(birthDate); // Anda perlu tambah field ini dalam UserProfile
        if (!profileDao.save(profile)) {
            throw new RuntimeException("Gagal menyimpan profil pengguna");
        }

        // Auto-tambah user utama ke family_members (untuk Customer sahaja)
        if ("CUSTOMER".equalsIgnoreCase(role)) {
            if (!familyMemberDao.hasMainUser(userId)) {
                String userName = getUserNameFromDatabase(userId);
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
     * Simpan profil asas (untuk Tailor atau Customer tanpa tarikh lahir)
     */
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

    /**
     * Dapatkan profil pengguna
     */
    public UserProfile getProfile(String userId) {
        return profileDao.findByUserId(userId);
    }

    /**
     * Semak sama ada profil lengkap
     */
    public boolean isProfileComplete(String userId, String role) {
        return profileDao.isProfileComplete(userId, role);
    }

    /**
     * Dapatkan nama pengguna dari database
     * Anda perlu implementasikan method ini dalam UserDao
     */
    private String getUserNameFromDatabase(String userId) {
        // Contoh: Anda perlu tambah method dalam UserDao
        // UserDao userDao = new UserDaoImpl();
        // return userDao.findNameById(userId);
        
        // Untuk sementara, kembalikan placeholder
        // GANTIKAN DENGAN IMPLEMENTASI SEBENAR
        throw new UnsupportedOperationException("Method getUserNameFromDatabase belum diimplementasikan");
    }
}