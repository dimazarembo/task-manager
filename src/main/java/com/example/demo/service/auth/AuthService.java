package com.example.demo.service.auth;

import com.example.demo.controllers.auth.JwtResponse;
import com.example.demo.controllers.auth.LoginRequest;
import com.example.demo.controllers.auth.RegistrationRequest;
import com.example.demo.security.jwt.JwtService;
import com.example.demo.user.Role;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        User user = userRepository.findByUsername(loginRequest.username()).orElseThrow();
        String token = jwtService.generate(user.getUsername(), user.getRole().name());
        return new JwtResponse(token);
    }

    @Transactional
    public void register(RegistrationRequest registrationRequest) {
        if (userRepository.findByUsername(registrationRequest.username()).isPresent()) {
            throw new IllegalArgumentException("Username is already in use");
        }
        User user = new User();
        user.setUsername(registrationRequest.username());
        user.setEmail(registrationRequest.email());
        user.setPassword(passwordEncoder.encode(registrationRequest.password()));
        user.setRole(Role.USER);
        userRepository.save(user);


    }
}
