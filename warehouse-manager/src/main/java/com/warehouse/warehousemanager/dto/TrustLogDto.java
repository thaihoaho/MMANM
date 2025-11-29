package com.warehouse.warehousemanager.dto;

import java.time.LocalDateTime;

public class TrustLogDto {
    private Long id;
    private Long userId;
    private String username;
    private String resource;
    private String action;
    private String ipAddress;
    private Double trustScore;
    private Boolean decisionResult;
    private String reason;
    private LocalDateTime timestamp;

    public TrustLogDto() {}

    public TrustLogDto(Long id, Long userId, String username, String resource, String action, 
                      String ipAddress, Double trustScore, Boolean decisionResult, String reason, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.resource = resource;
        this.action = action;
        this.ipAddress = ipAddress;
        this.trustScore = trustScore;
        this.decisionResult = decisionResult;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Double getTrustScore() {
        return trustScore;
    }

    public void setTrustScore(Double trustScore) {
        this.trustScore = trustScore;
    }

    public Boolean getDecisionResult() {
        return decisionResult;
    }

    public void setDecisionResult(Boolean decisionResult) {
        this.decisionResult = decisionResult;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}