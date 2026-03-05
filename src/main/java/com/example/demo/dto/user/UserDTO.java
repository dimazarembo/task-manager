package com.example.demo.dto.user;

import com.example.demo.repositories.user.Role;

public record UserDTO(Long id, String username, String email, Role role) {
}
