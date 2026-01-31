// com.tailorshop.dao.UserDao.java
package com.tailorshop.dao;

import com.tailorshop.model.User;
import java.util.List;

public interface UserDao {
    
    /**
     * Cari pengguna dengan emel dan kata laluan
     */
    User findByEmailAndPassword(String email, String password);
    
    /**
     * Cari pengguna dengan emel
     */
    User findByEmail(String email);
    
    /**
     * Dapatkan nama pengguna mengikut ID
     */
    String findNameById(String userId);
    
    /**
     * Simpan pengguna baharu
     */
    boolean save(User user);
    
    /**
     * Semak jika pengguna wujud dengan nama dan emel
     */
    boolean userExistsByNameAndEmail(String name, String email);
    
    /**
     * Kemas kini kata laluan
     */
    boolean updatePassword(String email, String newPassword);
    
    /**
     * Jana ID pengguna berikutnya
     */
    String generateNextId(String role);
    
    /**
     * ✅ DAPATKAN SEMUA PELANGGAN (untuk Tailor)
     */
    List<User> getAllCustomers();
    
    /**
     * ✅ DAPATKAN SEMUA TAILOR (untuk Customer benarkan akses)
     */
    List<User> getAllTailors();
}