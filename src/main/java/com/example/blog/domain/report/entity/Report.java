package com.example.blog.domain.report.entity;

import com.example.blog.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private Member reporter;

    @Column(nullable = false)
    private String reporterNickname;

    @ManyToOne
    @JoinColumn(name = "reported_id", nullable = false)
    private Member reported;

    @Column(nullable = false)
    private String reportedNickname;

    private String reason;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author;



}