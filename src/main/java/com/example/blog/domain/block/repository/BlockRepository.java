package com.example.blog.domain.block.repository;

import com.example.blog.domain.block.entity.Block;
import com.example.blog.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BlockRepository extends JpaRepository<Block, Long> {

    Optional<Block> findByBlockerAndBlocked(Member blocker, Member blocked);

    boolean existsByBlockerNicknameAndBlockedNickname(String blockerNickname, String blockedNickname);

}
