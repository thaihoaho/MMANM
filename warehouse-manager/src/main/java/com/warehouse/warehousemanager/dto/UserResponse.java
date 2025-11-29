package com.warehouse.warehousemanager.dto;

import com.warehouse.warehousemanager.entity.User;

import java.time.LocalDateTime;
import java.util.Set;

public class UserResponse {
    private Long id;
    private String username;
    private String role;
    private Set<String> permissions;
    private LocalDateTime createdAt;

    public UserResponse() {}

    public UserResponse(Long id, String username, String role, Set<String> permissions, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.permissions = permissions;
        this.createdAt = createdAt;
    }

    // Constructor that takes a User entity
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole().name();
        this.permissions = user.getPermissions();
        this.createdAt = user.getCreatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}