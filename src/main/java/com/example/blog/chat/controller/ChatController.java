package com.example.blog.chat.controller;

import com.example.blog.chat.model.ChatMessage;
import com.example.blog.chat.repository.ChatMessageRepository;
import com.example.blog.chat.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatMessageService messageService;

    @MessageMapping("/chat/message/{roomId}")
    @SendTo("/sub/chat/room/{roomId}")
    public ChatMessage sendMessage(@Payload ChatMessage message, @DestinationVariable("roomId") String roomId) {
        try {
            if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
                message.setMessage(message.getSender() + "님이 입장하셨습니다.");
            }
            message.setRoomId(roomId);
            return messageService.save(message);
        } catch (Exception e) {

            return null;
        }
    }
//    @GetMapping("/chat/messages/{roomId}")
//    public ResponseEntity<List<Message>> getMessages(@PathVariable("roomId") String roomId) {
//        List<Message> messages = chatMessageRepository.findByRoomIdOrderByTimestampDesc(roomId);
//        return ResponseEntity.ok(messages);
//    }

    @MessageMapping("/chat/private/{roomId}")
    @SendTo("/sub/chat/private/{roomId}")
    public ChatMessage sendPrivateMessage(@Payload ChatMessage message, @DestinationVariable("roomId") String roomId) {
        message.setRoomId(roomId);
        return messageService.save(message);
    }

}