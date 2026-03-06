package com.example.demo.service.auth;

import com.example.demo.dto.auth.JwtResponse;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.RegistrationRequest;
import com.example.demo.dto.user.UserResponse;
import com.example.demo.repositories.user.Role;
import com.example.demo.security.jwt.JwtService;
import com.example.demo.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_returnsJwt_whenCredentialsValid() {
        LoginRequest request = new LoginRequest("admin", "password");
        UserResponse user = new UserResponse(1L, "admin", "admin@mail.com", Role.ADMIN);

        when(userService.findUserByUsername("admin")).thenReturn(user);
        when(jwtService.generate("admin", "ADMIN")).thenReturn("jwt-token");

        JwtResponse response = authService.login(request);

        assertEquals("jwt-token", response.token());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("admin", captor.getValue().getPrincipal());
        assertEquals("password", captor.getValue().getCredentials());
        verify(userService).findUserByUsername("admin");
        verify(jwtService).generate("admin", "ADMIN");
    }

    @Test
    void login_throws_whenAuthenticationFails() {
        LoginRequest request = new LoginRequest("admin", "wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verifyNoInteractions(userService);
        verifyNoInteractions(jwtService);
    }

    @Test
    void register_delegatesToUserService() {
        RegistrationRequest request = new RegistrationRequest("new-user", "new@mail.com", "password123");
        UserResponse expected = new UserResponse(10L, "new-user", "new@mail.com", Role.USER);

        when(userService.createUser("new-user", "password123", "new@mail.com")).thenReturn(expected);

        UserResponse actual = authService.register(request);

        assertSame(expected, actual);
        verify(userService).createUser("new-user", "password123", "new@mail.com");
    }
}
