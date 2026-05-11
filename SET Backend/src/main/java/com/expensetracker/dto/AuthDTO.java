package com.expensetracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDTO {

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        private String currency = "INR";
        private Double monthlyBudget;
    }

    @Data
    public static class LoginRequest {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String name;
        private String email;
        private String currency;
        private Double monthlyBudget;

        public AuthResponse(String token, Long id, String name, String email, String currency, Double monthlyBudget) {
            this.token = token;
            this.id = id;
            this.name = name;
            this.email = email;
            this.currency = currency;
            this.monthlyBudget = monthlyBudget;
        }
    }
}
