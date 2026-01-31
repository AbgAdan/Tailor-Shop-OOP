// com.tailorshop.controller.UserController.java
package com.tailorshop.controller;

import com.tailorshop.dao.UserDao;
import com.tailorshop.DaoImpl.UserDaoImpl;
import com.tailorshop.model.User;

import java.util.List;

public class UserController {
    private UserDao userDao = new UserDaoImpl();

    /**
     * Daftar pengguna sebagai CUSTOMER (dari RegisterPanel)
     */
    public void registerCustomer(String name, String email, String password) throws Exception {
        validateInput(name, email, password, "CUSTOMER");
        
        if (userDao.findByEmail(email) != null) {
            throw new IllegalStateException("Emel sudah digunakan");
        }

        User newUser = new User(name, email, password, "CUSTOMER");
        if (!userDao.save(newUser)) {
            throw new RuntimeException("Gagal menyimpan pengguna");
        }
    }

    /**
     * Daftar pengguna oleh BOSS (password auto = "123456")
     */
    public void registerByBoss(String name, String email, String role, String bossId) throws Exception {
        if (bossId == null || bossId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID Boss diperlukan");
        }
        validateInput(name, email, "123456", role); // guna password dummy untuk validasi panjang

        if (userDao.findByEmail(email) != null) {
            throw new IllegalStateException("Emel sudah digunakan");
        }

        // 🔑 Tetapkan password default
        String defaultPassword = "123456";
        User newUser = new User(name, email, defaultPassword, role);
        newUser.setRegisteredBy(bossId);

        if (!userDao.save(newUser)) {
            throw new RuntimeException("Gagal menyimpan pengguna");
        }
    }

    // Helper validation
    private void validateInput(String name, String email, String password, String role) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama diperlukan");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Emel diperlukan");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Kata laluan mesti sekurang-kurangnya 6 aksara");
        }
        if (!isValidRole(role)) {
            throw new IllegalArgumentException("Peranan tidak sah: " + role);
        }
    }

    private boolean isValidRole(String role) {
        return "CUSTOMER".equals(role) || "TAILOR".equals(role) || "BOSS".equals(role);
    }
    
    // ✅ METHOD BARU: DAPATKAN SEMUA PELANGGAN
    public List<User> getAllCustomers() {
        return userDao.getAllCustomers(); // ✅ BETUL - guna userDao
    }
    
    // ✅ METHOD BARU: DAPATKAN SEMUA TAILOR
    public List<User> getAllTailors() {
        return userDao.getAllTailors(); // ✅ TAMBAH INI
    }
}