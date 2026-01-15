package com.tqtadka.platform.entity;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled;

    /* ===============================
       ✅ ALLOWED CATEGORIES
    =============================== */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "user_allowed_categories",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "category")
    private Set<CategoryType> allowedCategories = new HashSet<>();

    /* ===============================
       HELPERS
    =============================== */
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public Set<CategoryType> getEffectiveCategories() {
        return isAdmin()
                ? EnumSet.allOf(CategoryType.class)
                : allowedCategories;
    }
}