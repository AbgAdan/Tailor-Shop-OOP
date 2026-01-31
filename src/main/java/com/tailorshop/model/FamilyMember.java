// com.tailorshop.model.FamilyMember.java
package com.tailorshop.model;

import java.time.LocalDate;
import java.time.Period;

public class FamilyMember {
    private int id;
    private String customerId;
    private String name;
    private String gender;
    private String birthDate; // format: "yyyy-MM-dd"
    private boolean isMainUser;
    private boolean managedByTailor; // ✅ BARU
    private String tailorId;         // ✅ BARU

    // Constructor kosong
    public FamilyMember() {}

    // Constructor untuk create baru
    public FamilyMember(String customerId, String name, String gender, String birthDate, boolean isMainUser) {
        this.customerId = customerId;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.isMainUser = isMainUser;
        this.managedByTailor = false; // default
        this.tailorId = null;
    }

    // Constructor penuh
    public FamilyMember(int id, String customerId, String name, String gender, String birthDate, 
                      boolean isMainUser, boolean managedByTailor, String tailorId) {
        this.id = id;
        this.customerId = customerId;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.isMainUser = isMainUser;
        this.managedByTailor = managedByTailor;
        this.tailorId = tailorId;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public boolean isMainUser() { return isMainUser; }
    public void setMainUser(boolean mainUser) { isMainUser = mainUser; }

    // ✅ GETTER & SETTER BARU
    public boolean isManagedByTailor() { return managedByTailor; }
    public void setManagedByTailor(boolean managedByTailor) { this.managedByTailor = managedByTailor; }

    public String getTailorId() { return tailorId; }
    public void setTailorId(String tailorId) { this.tailorId = tailorId; }

    // Helper method untuk kira umur
    public int getAge() {
        if (birthDate == null || birthDate.trim().isEmpty()) {
            return 0;
        }
        try {
            LocalDate birth = LocalDate.parse(birthDate);
            LocalDate now = LocalDate.now();
            return Period.between(birth, now).getYears();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "FamilyMember{" +
                "id=" + id +
                ", customerId='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", isMainUser=" + isMainUser +
                ", managedByTailor=" + managedByTailor +
                ", tailorId='" + tailorId + '\'' +
                '}';
    }
}