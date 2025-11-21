package com.warehouse.warehousemanager.controller;

import com.warehouse.warehousemanager.dto.ApiResponse;
import com.warehouse.warehousemanager.dto.UserDto;
import com.warehouse.warehousemanager.entity.User;
import com.warehouse.warehousemanager.security.policy.PolicyEnforcementPoint;
import com.warehouse.warehousemanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PolicyEnforcementPoint policyEnforcementPoint;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            HttpServletRequest request) {

        // Check policy enforcement
        if (!policyEnforcementPoint.checkAccess("users", "read", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        List<User> users = userService.findAll();
        // In a real implementation, we would use Pageable and map to DTOs
        // For simplicity, returning the entities directly
        List<UserDto> userDtos = users.stream()
            .map(user -> new UserDto(user.getId(), user.getUsername(), user.getRole().name(), user.getPermissions(), user.getCreatedAt()))
            .toList();

        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("users", "read", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getRole().name(), user.getPermissions(), user.getCreatedAt());
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userDto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createUser(@RequestBody User user, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("users", "create", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        User savedUser = userService.save(user);
        UserDto userDto = new UserDto(savedUser.getId(), savedUser.getUsername(), savedUser.getRole().name(), savedUser.getPermissions(), savedUser.getCreatedAt());
        return ResponseEntity.ok(ApiResponse.success("User created successfully", userDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable Long id, @RequestBody User userDetails, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("users", "update", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        User updatedUser = userService.update(id, userDetails);
        UserDto userDto = new UserDto(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getRole().name(), updatedUser.getPermissions(), updatedUser.getCreatedAt());
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("users", "delete", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        userService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }
}