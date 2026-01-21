// com.tailorshop.model.User
package com.tailorshop.model;

public class User {
    private String name;
    private String email;
    private String password;
    private String role;

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getter
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}