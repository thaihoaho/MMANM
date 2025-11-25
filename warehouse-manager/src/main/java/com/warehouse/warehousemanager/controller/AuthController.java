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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/teleport")
    public ResponseEntity<Map<String, Object>> getTeleportIdentity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // User đã authenticated qua Teleport
            try {
                // Lấy user từ database nếu có
                User user = userService.findByUsername(auth.getName()).orElse(null);
                
                if (user != null) {
                    // User có trong database
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("username", user.getUsername());
                    userMap.put("role", user.getRole().name());
                    userMap.put("permissions", user.getPermissions() != null ? user.getPermissions() : java.util.Collections.emptyList());
                    response.put("user", userMap);
                } else {
                    // User không có trong DB, tạo từ Teleport identity
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", 0);
                    userMap.put("username", auth.getName());
                    
                    // Extract role from authorities
                    String role = "USER";
                    if (auth.getAuthorities() != null) {
                        String roleAuthority = auth.getAuthorities().stream()
                            .map(a -> a.getAuthority())
                            .filter(a -> a.startsWith("ROLE_"))
                            .findFirst()
                            .orElse("ROLE_USER");
                        role = roleAuthority.replace("ROLE_", "");
                    }
                    userMap.put("role", role);
                    
                    // Extract permissions from authorities
                    java.util.List<String> permissions = auth.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.toList());
                    userMap.put("permissions", permissions);
                    
                    response.put("user", userMap);
                }
                
                response.put("authenticated", true);
            } catch (Exception e) {
                response.put("authenticated", false);
                response.put("message", "Error getting user info: " + e.getMessage());
            }
        } else {
            response.put("authenticated", false);
            response.put("message", "Not authenticated via Teleport");
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAuthServiceInfo() {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("login", "/api/auth/login");
        endpoints.put("refresh", "/api/auth/refresh");
        endpoints.put("teleport", "/api/auth/teleport");
        endpoints.put("register", "/api/auth/register");

        response.put("service", "Warehouse Auth Service");
        response.put("status", "running");
        response.put("version", "1.0.0");
        response.put("endpoints", endpoints);

        return ResponseEntity.ok(response);
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