// com.tailorshop.model.UserProfile.java
package com.tailorshop.model;

import java.time.LocalDate;

public class UserProfile {
    private String userId;
    private String gender;
    private String phone;
    private String address;
    private LocalDate birthDate; // ← TAMBAH INI

    public UserProfile(String userId, String phone) {
        this.userId = userId;
        this.phone = phone;
    }

    // Getter & Setter
    public String getUserId() { return userId; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    // 🔑 TAMBAH INI
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}