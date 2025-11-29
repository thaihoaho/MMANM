package com.warehouse.warehousemanager.controller;

import com.warehouse.warehousemanager.dto.ApiResponse;
import com.warehouse.warehousemanager.dto.TrustLogDto;
import com.warehouse.warehousemanager.entity.TrustLog;
import com.warehouse.warehousemanager.security.policy.PolicyEnforcementPoint;
import com.warehouse.warehousemanager.service.TrustLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trust-logs")
@CrossOrigin(origins = "*")
public class TrustLogController {

    @Autowired
    private TrustLogService trustLogService;

    @Autowired
    private PolicyEnforcementPoint policyEnforcementPoint;

    // Get all trust logs
    @GetMapping
    public ResponseEntity<ApiResponse<List<TrustLogDto>>> getAllTrustLogs(HttpServletRequest request) {
        System.out.println("TrustLogController.getAllTrustLogs called");

        // Skip policy enforcement for reading trust logs to avoid infinite logging loops
        // Only authenticated users with proper JWT can access this endpoint
        // Admin role is required to access trust logs
        // Verify user is authenticated and isAdmin
        if (!isUserAdmin(request)) {
            System.out.println("TrustLogController: Access denied - user not admin");
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        System.out.println("TrustLogController: Access granted, retrieving trust logs");
        List<TrustLogDto> trustLogs = trustLogService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Trust logs retrieved successfully", trustLogs));
    }

    // Get paginated trust logs
    @GetMapping(params = {"page", "size"})
    public ResponseEntity<ApiResponse<Page<TrustLogDto>>> getPaginatedTrustLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String resource,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Boolean decisionResult,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String username,
            HttpServletRequest request) {
        System.out.println("TrustLogController.getPaginatedTrustLogs called with page: " + page + ", size: " + size);

        if (!policyEnforcementPoint.checkAccess("trust-logs", "read", request)) {
            System.out.println("TrustLogController: Access denied by policy enforcement");
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TrustLogDto> trustLogs;

        // Check if any filters are applied
        boolean hasFilters = resource != null || action != null || decisionResult != null ||
                            userId != null || username != null;

        if (hasFilters) {
            // Use the service method that supports combined filtering
            trustLogs = trustLogService.findPaginatedWithFilters(pageable, resource, action, decisionResult, userId, username);
        } else {
            trustLogs = trustLogService.findPaginated(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success("Paginated trust logs retrieved successfully", trustLogs));
    }

    // Helper method to check if user is admin without triggering policy enforcement (to avoid infinite loops)
    private boolean isUserAdmin(HttpServletRequest request) {
        // Get the authentication from security context
        org.springframework.security.core.Authentication authentication =
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Check if user has ADMIN role
        return authentication.getAuthorities().stream()
            .anyMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    // Get trust logs by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<TrustLogDto>>> getTrustLogsByUserId(@PathVariable Long userId, HttpServletRequest request) {
        // Skip policy enforcement for reading trust logs to avoid infinite logging loops
        if (!isUserAdmin(request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        List<TrustLogDto> trustLogs = trustLogService.findByUserIdAsDto(userId);
        return ResponseEntity.ok(ApiResponse.success("Trust logs retrieved successfully", trustLogs));
    }

    // Get paginated trust logs by user ID
    @GetMapping(value = "/user/{userId}", params = {"page", "size"})
    public ResponseEntity<ApiResponse<Page<TrustLogDto>>> getPaginatedTrustLogsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest request) {
        // Skip policy enforcement for reading trust logs to avoid infinite logging loops
        if (!isUserAdmin(request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TrustLogDto> trustLogs = trustLogService.findByUserIdPaginated(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Paginated trust logs retrieved successfully", trustLogs));
    }

    // Get trust logs by username
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<List<TrustLogDto>>> getTrustLogsByUsername(@PathVariable String username, HttpServletRequest request) {
        // Skip policy enforcement for reading trust logs to avoid infinite logging loops
        if (!isUserAdmin(request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        List<TrustLogDto> trustLogs = trustLogService.findByUsernameAsDto(username);
        return ResponseEntity.ok(ApiResponse.success("Trust logs retrieved successfully", trustLogs));
    }

    // Get paginated trust logs by username
    @GetMapping(value = "/username/{username}", params = {"page", "size"})
    public ResponseEntity<ApiResponse<Page<TrustLogDto>>> getPaginatedTrustLogsByUsername(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest request) {
        // Skip policy enforcement for reading trust logs to avoid infinite logging loops
        if (!isUserAdmin(request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TrustLogDto> trustLogs = trustLogService.findByUsernamePaginated(username, pageable);
        return ResponseEntity.ok(ApiResponse.success("Paginated trust logs retrieved successfully", trustLogs));
    }

    // Get trust logs by resource
    @GetMapping("/resource/{resource}")
    public ResponseEntity<ApiResponse<List<TrustLogDto>>> getTrustLogsByResource(@PathVariable String resource, HttpServletRequest request) {
        // Skip policy enforcement for reading trust logs to avoid infinite logging loops
        if (!isUserAdmin(request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        List<TrustLogDto> trustLogs = trustLogService.findByResourceAsDto(resource);
        return ResponseEntity.ok(ApiResponse.success("Trust logs retrieved successfully", trustLogs));
    }

    // Get trust logs by action
    @GetMapping("/action/{action}")
    public ResponseEntity<ApiResponse<List<TrustLogDto>>> getTrustLogsByAction(@PathVariable String action, HttpServletRequest request) {
        // Skip policy enforcement for reading trust logs to avoid infinite logging loops
        if (!isUserAdmin(request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        List<TrustLogDto> trustLogs = trustLogService.findByActionAsDto(action);
        return ResponseEntity.ok(ApiResponse.success("Trust logs retrieved successfully", trustLogs));
    }

    // Get trust logs by decision result (true for granted, false for denied)
    @GetMapping("/decision/{decisionResult}")
    public ResponseEntity<ApiResponse<List<TrustLogDto>>> getTrustLogsByDecisionResult(@PathVariable Boolean decisionResult, HttpServletRequest request) {
        // Skip policy enforcement for reading trust logs to avoid infinite logging loops
        if (!isUserAdmin(request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        List<TrustLogDto> trustLogs = trustLogService.findByDecisionResultAsDto(decisionResult);
        return ResponseEntity.ok(ApiResponse.success("Trust logs retrieved successfully", trustLogs));
    }

    // Get trust logs by time range
    @GetMapping("/time-range")
    public ResponseEntity<ApiResponse<List<TrustLogDto>>> getTrustLogsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            HttpServletRequest request) {
        // Skip policy enforcement for reading trust logs to avoid infinite logging loops
        if (!isUserAdmin(request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        List<TrustLogDto> trustLogs = trustLogService.findByTimestampBetweenAsDto(start, end);
        return ResponseEntity.ok(ApiResponse.success("Trust logs retrieved successfully", trustLogs));
    }

    // Get trust logs by user and time range
    @GetMapping("/user/{userId}/time-range")
    public ResponseEntity<ApiResponse<List<TrustLogDto>>> getTrustLogsByUserAndTimeRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            HttpServletRequest request) {
        // Skip policy enforcement for reading trust logs to avoid infinite logging loops
        if (!isUserAdmin(request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        List<TrustLogDto> trustLogs = trustLogService.findByUserIdAndTimestampBetweenAsDto(userId, start, end);
        return ResponseEntity.ok(ApiResponse.success("Trust logs retrieved successfully", trustLogs));
    }

    // Get trust logs by username and time range
    @GetMapping("/username/{username}/time-range")
    public ResponseEntity<ApiResponse<List<TrustLogDto>>> getTrustLogsByUsernameAndTimeRange(
            @PathVariable String username,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            HttpServletRequest request) {
        // Skip policy enforcement for reading trust logs to avoid infinite logging loops
        if (!isUserAdmin(request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        List<TrustLogDto> trustLogs = trustLogService.findByUsernameAndTimestampBetweenAsDto(username, start, end);
        return ResponseEntity.ok(ApiResponse.success("Trust logs retrieved successfully", trustLogs));
    }
}