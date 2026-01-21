// com.tailorshop.dao.UserDao
package com.tailorshop.dao;

import com.tailorshop.model.User;

public interface UserDao {
    
    User findByEmailAndPassword(String email, String password);
    User findByEmail(String email);
    
    boolean save(User user);
    
    boolean userExistsByNameAndEmail(String name, String email);
    boolean updatePassword(String email, String newPassword);
}