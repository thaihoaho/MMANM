package com.example.warehouse_management.pdp.model;

import com.example.warehouse_management.entity.User;

public class RequestContext {
    private User user;
    private String resource;
    private String action;
    private Object resourceObject; // The actual resource being accessed (e.g., Product, ImportSlip)
    private String environment; // Additional context info

    public RequestContext(User user, String resource, String action) {
        this.user = user;
        this.resource = resource;
        this.action = action;
    }

    public RequestContext(User user, String resource, String action, Object resourceObject) {
        this.user = user;
        this.resource = resource;
        this.action = action;
        this.resourceObject = resourceObject;
    }

    // Getters and setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getResourceObject() {
        return resourceObject;
    }

    public void setResourceObject(Object resourceObject) {
        this.resourceObject = resourceObject;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}