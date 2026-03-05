package com.example.demo.controllers.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank(message = "username is required")
        @Size(min = 3, max = 50, message = "username length must be between 3 and 50")
        String username,
        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 255, message = "email is too long")
        String email,
        @NotBlank(message = "password is required")
        @Size(min = 6, max = 72, message = "password length must be between 6 and 72")
        String password
) {
}
