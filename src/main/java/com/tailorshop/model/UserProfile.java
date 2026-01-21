// com.tailorshop.model.UserProfile
package com.tailorshop.model;

public class UserProfile {
    private String userId;
    private String gender;      // null jika bukan customer
    private String phone;       // wajib
    private String address;     // null jika bukan customer

    // Constructor
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
}