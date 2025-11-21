package com.example.warehouse_management.pdp.model;

public class Decision {
    private boolean permitted;
    private String reason;
    private String policyId;

    public Decision(boolean permitted, String reason, String policyId) {
        this.permitted = permitted;
        this.reason = reason;
        this.policyId = policyId;
    }

    // Getters and setters
    public boolean isPermitted() {
        return permitted;
    }

    public void setPermitted(boolean permitted) {
        this.permitted = permitted;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }
}