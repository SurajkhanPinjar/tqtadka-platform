package com.tqtadka.platform.service;

import com.tqtadka.platform.entity.Role;
import com.tqtadka.platform.entity.User;
import com.tqtadka.platform.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* =========================
       PUBLIC REGISTRATION
       (AUTHOR only)
    ========================= */
    public void register(String name, String email, String rawPassword) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .name(name.trim())
                .email(email.trim().toLowerCase())
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.AUTHOR)   // default role
                .enabled(true)
                .build();

        userRepository.save(user);
    }

    /* =========================
       COMMON UTIL METHODS
    ========================= */
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findAllAuthors() {
        return userRepository.findAll()
                .stream()
                .filter(u ->
                        u.getRole() == Role.AUTHOR ||
                                u.getRole() == Role.ADMIN
                )
                .toList();
    }
}