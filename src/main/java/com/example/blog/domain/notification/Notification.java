package com.example.blog.domain.notification;

import com.example.blog.domain.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Notification {
    // Setters
    // Getters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private SiteUser sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private SiteUser receiver;

    private String message;
    private boolean accepted;

}
