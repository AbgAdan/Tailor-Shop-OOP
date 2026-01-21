// com.tailorshop.controller.AuthController
package com.tailorshop.controller;

import com.tailorshop.dao.UserDao;
import com.tailorshop.DaoImpl.UserDaoImpl;
import com.tailorshop.model.User;

public class AuthController {
    private UserDao userDao = new UserDaoImpl();

    public User login(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Emel dan kata laluan diperlukan");
        }
        return userDao.findByEmailAndPassword(email, password);
    }
}