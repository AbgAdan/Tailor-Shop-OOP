// com.tailorshop.model.FamilyMember.java
package com.tailorshop.model;

import java.time.LocalDate;
import java.time.Period;

public class FamilyMember {
    private int id;
    private String customerId;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private boolean isMainUser;

    public FamilyMember(String customerId, String name, String gender, LocalDate birthDate, boolean isMainUser) {
        this.customerId = customerId;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.isMainUser = isMainUser;
    }

    // Getter
    public int getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public LocalDate getBirthDate() { return birthDate; }
    public boolean isMainUser() { return isMainUser; }

    // Setter
    public void setId(int id) { this.id = id; }

    // Kira umur
    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}