package com.example.demo.dto.user;

import com.example.demo.repositories.user.Role;

public record UserResponse(Long id, String username, String email, Role role) {
}
