package com.example.blog.domain.block.entity;

import com.example.blog.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "blocker_id", nullable = false)
    private Member blocker;

    @Column(nullable = false)
    private String blockerNickname;


    @ManyToOne
    @JoinColumn(name = "blocked_id", nullable = false)
    private Member blocked;

    @Column(nullable = false)
    private String blockedNickname;
}