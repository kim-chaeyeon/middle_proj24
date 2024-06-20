package com.example.blog.domain.restaurantComment;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RCRepository extends JpaRepository<RC, Integer> {
    @Transactional
    @Modifying
    @Query(value = "ALTER TABLE rc AUTO_INCREMENT = 1", nativeQuery = true)
    void clearAutoIncrement();
}