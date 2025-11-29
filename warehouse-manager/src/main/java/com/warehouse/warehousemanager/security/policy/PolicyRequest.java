package com.warehouse.warehousemanager.security.policy;

import com.warehouse.warehousemanager.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public class PolicyRequest {
    private User user;
    private String resource;
    private String action;
    private HttpServletRequest request;
    private String ipAddress;
    private int hourOfDay;
    private double riskScore;

    // Constructors
    public PolicyRequest() {}

    public PolicyRequest(User user, String resource, String action, HttpServletRequest request) {
        this.user = user;
        this.resource = resource;
        this.action = action;
        this.request = request;
    }

    // Getters and Setters
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

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }
}