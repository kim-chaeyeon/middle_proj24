package com.example.blog.chat.service;


import com.example.blog.chat.model.ChatMessage;
import com.example.blog.chat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {
    @Autowired
    private ChatMessageRepository chatMessagerepository;

    public ChatMessage save(ChatMessage message) {
        return chatMessagerepository.save(message);
    }


}

