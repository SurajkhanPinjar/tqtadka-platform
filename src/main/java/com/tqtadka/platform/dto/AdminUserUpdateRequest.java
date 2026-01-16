package com.tqtadka.platform.dto;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.Role;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AdminUserUpdateRequest {

    private Long id;
    private String name;
    private Role role;
    private boolean enabled;
    private List<CategoryType> allowedCategories;

    // getters & setters
}