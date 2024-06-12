package com.example.blog.domain.comment;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Transactional
    @Modifying
    @Query(value = "ALTER TABLE Comment AUTO_INCREMENT = 1", nativeQuery = true)
    void clearAutoIncrement();
}
