package com.warehouse.warehousemanager.exception;

public class TokenRefreshException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenRefreshException(String token, String message) {
        super(String.format("Couldn't find valid refresh token: %s, %s", token, message));
    }
}