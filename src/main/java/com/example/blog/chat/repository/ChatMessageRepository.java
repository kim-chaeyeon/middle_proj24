package com.example.blog.chat.repository;

import com.example.blog.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
//    List<Message> findByRoomIdOrderByTimestampDesc(String roomId);
}
