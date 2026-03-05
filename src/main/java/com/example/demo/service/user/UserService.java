package com.example.demo.service.user;

import com.example.demo.dto.user.UserDTO;
import com.example.demo.dto.user.UserMapper;
import com.example.demo.repositories.user.Role;
import com.example.demo.repositories.user.User;
import com.example.demo.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserDTO findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return userMapper.toDto(user);
    }


    public boolean isUserExists(String username, String email) {
        return userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public UserDTO createUser(String username, String password, String email) {
        if (isUserExists(username, email)) {
            throw new IllegalArgumentException("Username is already in use");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
