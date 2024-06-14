package com.example.blog.domain.chat.repository;

import com.example.blog.domain.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
//    List<Message> findByRoomIdOrderByTimestampDesc(String roomId);
}
