package com.example.demo.service;

import com.example.demo.user.Role;

public record UserDTO(String name, String email, String password, Role role) {
}
