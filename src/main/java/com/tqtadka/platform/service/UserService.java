package com.tqtadka.platform.service;

import com.tqtadka.platform.entity.Role;
import com.tqtadka.platform.entity.User;
import com.tqtadka.platform.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String name, String email, String rawPassword) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(name.trim())
                .email(email.toLowerCase())
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.AUTHOR)   // default role
                .enabled(true)
                .build();

        userRepository.save(user);

        userRepository.save(user);
    }
}