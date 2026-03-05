package com.example.demo.security;

import com.example.demo.repositories.user.Role;
import com.example.demo.repositories.user.User;
import com.example.demo.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Создаем админа при старте приложения, данные берем из конфига
 */
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Value("${app.bootstrap.admin.enabled:true}")
    private boolean enabled;

    @Value("${app.bootstrap.admin.username:admin}")
    private String username;

    @Value("${app.bootstrap.admin.email:admin@local}")
    private String email;

    @Value("${app.bootstrap.admin.password:}")
    private String password;


    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (!enabled) return;
        User admin = User.builder().username(username).password(passwordEncoder.encode(password))
                .email(email).role(Role.ADMIN).build();

        userRepository.save(admin);
    }
}
