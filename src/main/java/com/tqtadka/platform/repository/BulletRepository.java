package com.tqtadka.platform.repository;

import com.tqtadka.platform.entity.PostBullet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BulletRepository extends JpaRepository<PostBullet, Long> {
}