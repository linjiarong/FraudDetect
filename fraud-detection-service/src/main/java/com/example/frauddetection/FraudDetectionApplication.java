package com.example.frauddetection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Fraud Detection Service.
 * 
 * This class serves as the entry point for the Spring Boot application. It initializes and starts the application context.
 * 
 * Features:
 * - Uses the `@SpringBootApplication` annotation to enable component scanning, auto-configuration, and Spring Boot features.
 * 
 * How to Run:
 * - Execute the `main` method to start the application.
 * - The application will load its configuration from `application.yml` or `application.properties` and initialize all components.
 * 
 * Example Usage:
 * ```bash
 * java -jar fraud-detection-service.jar
 * ```
 * 
 * Author: linjiarong
 * Date: 2025-3-28
 */
@SpringBootApplication
public class FraudDetectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(FraudDetectionApplication.class, args);
    }
}