// com.tailorshop.model.User
package com.tailorshop.model;

public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String registeredBy; // ← field baru

    // Constructor penuh
    public User(String id, String name, String email, String password, String role, String registeredBy) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.registeredBy = registeredBy;
    }

    // Constructor untuk pendaftaran biasa
    public User(String name, String email, String password, String role) {
        this(null, name, email, password, role, null);
    }

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // 🔑 Getter & Setter untuk registeredBy
    public String getRegisteredBy() { return registeredBy; }
    public void setRegisteredBy(String registeredBy) { this.registeredBy = registeredBy; }
}