package com.warehouse.warehousemanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "trust_logs")
@EntityListeners(AuditingEntityListener.class)
public class TrustLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore // Prevent serialization of the full User object to avoid Hibernate proxy issues
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false) // For logging purposes when user might be deleted
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "resource")
    private String resource;

    @Column(name = "action")
    private String action;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "trust_score") // Store as double (0.0000 to 1.0000)
    private Double trustScore;

    @Column(name = "decision_result")
    private Boolean decisionResult;

    @Column(name = "reason")
    private String reason; // Explanation for the trust decision

    @CreatedDate
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    // Constructors
    public TrustLog() {}

    public TrustLog(User user, String resource, String action, String ipAddress,
                    Double trustScore, Boolean decisionResult, String reason) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
        this.username = user != null ? user.getUsername() : "unknown";
        this.resource = resource;
        this.action = action;
        this.ipAddress = ipAddress;
        this.trustScore = trustScore;
        this.decisionResult = decisionResult;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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