package com.tqtadka.platform.service.impl;

import com.tqtadka.platform.dto.AdminUserCreateRequest;
import com.tqtadka.platform.dto.AdminUserUpdateRequest;
import com.tqtadka.platform.entity.User;
import com.tqtadka.platform.repository.UserRepository;
import com.tqtadka.platform.service.AdminUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ======================
       GET ALL USERS
       ====================== */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /* ======================
       CREATE USER
       ====================== */
    @Override
    public void createUser(AdminUserCreateRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(true);

        // ✅ FIXED: already Enum, no conversion needed
        if (request.getAllowedCategories() != null) {
            user.setAllowedCategories(new HashSet<>(request.getAllowedCategories()));
        } else {
            user.setAllowedCategories(new HashSet<>());
        }

        userRepository.save(user);
    }

    /* ======================
       UPDATE USER
       ====================== */
    @Override
    public void updateUser(AdminUserUpdateRequest request) {

        if (request.getId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setName(request.getName());
        user.setRole(request.getRole());
        user.setEnabled(request.isEnabled());

        // ✅ FIXED: List<CategoryType> → Set<CategoryType>
        if (request.getAllowedCategories() != null) {
            user.setAllowedCategories(new HashSet<>(request.getAllowedCategories()));
        } else {
            user.setAllowedCategories(new HashSet<>());
        }

        userRepository.save(user);
    }

    /* ======================
       DELETE USER
       ====================== */
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    /* ======================
       GET USER FOR EDIT
       ====================== */
    @Override
    public User getUserForEdit(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}