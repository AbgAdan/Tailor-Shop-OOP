// com.tailorshop.controller.UserController
package com.tailorshop.controller;

import com.tailorshop.dao.UserDao;
import com.tailorshop.DaoImpl.UserDaoImpl;
import com.tailorshop.model.User;

public class UserController {
    private UserDao userDao = new UserDaoImpl();

    public void registerCustomer(String name, String email, String password) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama diperlukan");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Emel diperlukan");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Kata laluan mesti sekurang-kurangnya 6 aksara");
        }

        // Semak jika emel sudah wujud
        if (userDao.findByEmail(email) != null) {
            throw new IllegalStateException("Emel sudah digunakan");
        }

        User newUser = new User(name, email, password, "CUSTOMER");
        if (!userDao.save(newUser)) {
            throw new RuntimeException("Gagal menyimpan pengguna");
        }
    }
}