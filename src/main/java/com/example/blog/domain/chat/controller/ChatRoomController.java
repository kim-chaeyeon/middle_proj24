package com.example.blog.domain.chat.controller;

import com.example.blog.domain.chat.model.ChatRoom;
import com.example.blog.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

    @GetMapping("/rooms")
    public String rooms(Model model) {
        return "room";  // Assuming 'room.html' is under the 'src/main/resources/templates' directory
    }

    @GetMapping("/api/rooms")
    @ResponseBody
    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }


    @PostMapping("/room")
    @ResponseBody
    public ResponseEntity<ChatRoom> createRoom(@RequestParam("name") String name) {
        ChatRoom newRoom = new ChatRoom();
        newRoom.setName(name);
        ChatRoom savedRoom = chatRoomRepository.save(newRoom);
        return ResponseEntity.ok(savedRoom);
    }

    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable("roomId") UUID roomId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(roomId);
        chatRoom.ifPresent(room -> model.addAttribute("roomId", room.getRoomId()));
        return chatRoom.isPresent() ? "roomdetail" : "redirect:/chat/rooms";
    }

    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable UUID roomId) {
        return chatRoomRepository.findById(roomId).orElse(null);
    }
}