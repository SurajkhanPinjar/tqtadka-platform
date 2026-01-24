package com.tqtadka.platform.repository;

import com.tqtadka.platform.entity.PostViewEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostViewEventRepository
        extends JpaRepository<PostViewEvent, Long> {
}