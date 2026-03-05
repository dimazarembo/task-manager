package com.example.demo.controllers;

import com.example.demo.security.jwt.JwtResponse;
import com.example.demo.security.jwt.JwtService;
import com.example.demo.security.jwt.LoginRequest;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest loginRequest) throws Exception {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        User user = userRepository.findByUsername(loginRequest.username()).orElseThrow();
        String token = jwtService.generate(user.getUsername(), user.getRole().name());
        return new JwtResponse(token);
    }
}
