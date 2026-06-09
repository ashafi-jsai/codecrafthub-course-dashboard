package net.courses;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main entry point for CodeCraftHub - a personalized learning platform.
 * The @SpringBootApplication annotation enables auto-configuration and
 * component scanning for all classes inside the net.courses package.
 */
@SpringBootApplication
public class CodeCraftHubApplication {

    @Value("${courses.file.path:courses.json}")
    private String filePath;

    public static void main(String[] args) {
        SpringApplication.run(CodeCraftHubApplication.class, args);
    }

    // Prints a startup banner after Spring Boot finishes initializing
    @Bean
    public CommandLineRunner startupBanner() {
        return args -> {
            System.out.println("--------------------------------------------------");
            System.out.println("  CodeCraftHub API is starting...");
            System.out.println("  Data will be stored in: " + filePath);
            System.out.println("  API will be available at: http://localhost:8080");
            System.out.println("--------------------------------------------------");
        };
    }
}
