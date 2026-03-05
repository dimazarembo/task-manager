package com.example.demo.controllers;

import com.example.demo.service.UserDTO;
import com.example.demo.user.Role;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")

public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public User createUser(@RequestBody UserDTO user) {

        User newUser = User.builder().email("tets@email.com").password("password").username("dima").role(Role.ADMIN).build();
        return userRepository.save(newUser);


    }
}
