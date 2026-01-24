package com.tqtadka.platform.repository;

import com.tqtadka.platform.entity.Role;
import com.tqtadka.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    /* =========================
       ADMIN / DASHBOARD QUERIES
    ========================= */

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);


        @Query("""
        select u
        from User u
        where u.role = com.tqtadka.platform.entity.Role.ADMIN
           or u.role = com.tqtadka.platform.entity.Role.AUTHOR
        order by u.name asc
    """)
        List<User> findAllAuthors();
}