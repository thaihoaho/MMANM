package com.warehouse.warehousemanager.controller;

import com.warehouse.warehousemanager.dto.LoginResponse;
import com.warehouse.warehousemanager.dto.UserResponse;
import com.warehouse.warehousemanager.dto.auth.LoginRequest;
import com.warehouse.warehousemanager.entity.RefreshToken;
import com.warehouse.warehousemanager.entity.User;
import com.warehouse.warehousemanager.service.RefreshTokenService;
import com.warehouse.warehousemanager.service.UserService;
import com.warehouse.warehousemanager.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtil.generateToken(loginRequest.getUsername());

        User user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        UserResponse userResponse = new UserResponse(user);

        LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken.getToken(), userResponse);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (userService.findByUsername(user.getUsername()).isPresent()) {
            response.put("error", "Username already exists");
            return ResponseEntity.badRequest().body(response);
        }

        User savedUser = userService.save(user);

        response.put("message", "User registered successfully");
        response.put("user", savedUser);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(refreshToken -> {
                    User user = refreshToken.getUser();

                    String accessToken = jwtUtil.generateToken(user.getUsername());

                    UserResponse userResponse = new UserResponse(user);

                    LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken.getToken(), userResponse);

                    return ResponseEntity.ok(loginResponse);
                })
                .orElseThrow(() -> new com.warehouse.warehousemanager.exception.TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }
}

class RefreshTokenRequest {
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}