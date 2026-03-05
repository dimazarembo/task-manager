package com.example.demo.service.auth;

import com.example.demo.controllers.auth.JwtResponse;
import com.example.demo.controllers.auth.LoginRequest;
import com.example.demo.controllers.auth.RegistrationRequest;
import com.example.demo.dto.user.UserDTO;
import com.example.demo.security.jwt.JwtService;
import com.example.demo.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public JwtResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        UserDTO user = userService.findUserByUsername(loginRequest.username());
        String token = jwtService.generate(user.username(), user.role().name());
        return new JwtResponse(token);
    }

    public UserDTO register(RegistrationRequest registrationRequest) {
        return userService.createUser(registrationRequest.username(),
                registrationRequest.password(),
                registrationRequest.email()
        );
    }
}
