package com.warehouse.warehousemanager.config;

import com.warehouse.warehousemanager.entity.User;
import com.warehouse.warehousemanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${security.default-admin-username:admin}")
    private String defaultAdminUsername;

    @Value("${security.default-admin-password:admin123}")
    private String defaultAdminPassword;

    @EventListener(ContextRefreshedEvent.class)
    public void seedData() {
        // Check if admin user already exists
        if (userService.findByUsername(defaultAdminUsername).isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername(defaultAdminUsername);
            adminUser.setPassword(passwordEncoder.encode(defaultAdminPassword));
            adminUser.setRole(User.Role.ADMIN);
            
            userService.save(adminUser);
        }
    }
}