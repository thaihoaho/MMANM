package com.warehouse.warehousemanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.warehouse.warehousemanager.repository")
public class DatabaseConfig {
    // Configuration for database and JPA
}