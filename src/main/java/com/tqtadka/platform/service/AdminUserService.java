package com.tqtadka.platform.service;

import com.tqtadka.platform.dto.AdminUserCreateRequest;
import com.tqtadka.platform.dto.AdminUserUpdateRequest;
import com.tqtadka.platform.entity.User;

import java.util.List;

public interface AdminUserService {

    List<User> getAllUsers();

    void createUser(AdminUserCreateRequest request);

    void updateUser(AdminUserUpdateRequest request);

    void deleteUser(Long userId);

    User getUserForEdit(Long userId);
}