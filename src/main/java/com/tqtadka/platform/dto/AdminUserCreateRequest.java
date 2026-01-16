package com.tqtadka.platform.dto;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.Role;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class AdminUserCreateRequest {

    private String name;
    private String email;
    private String password;
    private Role role;
    private Set<CategoryType> allowedCategories = new HashSet<>();

    // getters & setters
}