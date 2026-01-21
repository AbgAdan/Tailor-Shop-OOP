package com.tailorshop.controller;

import com.tailorshop.dao.UserDao;
import com.tailorshop.DaoImpl.UserDaoImpl;

public class ForgotPasswordController {
    private UserDao userDao = new UserDaoImpl();

    public boolean verifyUser(String name, String email) {
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama dan emel diperlukan");
        }
        return userDao.userExistsByNameAndEmail(name.trim(), email.trim());
    }

    public boolean resetPassword(String email, String newPassword) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Emel diperlukan");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Kata laluan mesti sekurang-kurangnya 6 aksara");
        }
        return userDao.updatePassword(email.trim(), newPassword);
    }
}