package com.example.blog.domain.chat.controller;


import com.example.blog.domain.chat.model.ChatRoom;
import com.example.blog.domain.chat.repository.ChatRoomRepository;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    private final Map<UUID, Set<String>> chatRoomMembers = new HashMap<>(); // 채팅방 멤버 목록


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
    public ChatRoom roomInfo(@PathVariable("roomId") String roomIdStr) {
        try {
            UUID roomId = UUID.fromString(roomIdStr);
            return chatRoomRepository.findById(roomId).orElse(null);
        } catch (IllegalArgumentException e) {
            // Handle invalid UUID string
            return null;
        }
    }
    @DeleteMapping("/room/{roomId}")
    @ResponseBody
    public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") UUID roomId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(roomId);
        if (chatRoom.isPresent()) {
            chatRoomRepository.deleteById(roomId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/room/{roomId}/enter")
    @ResponseBody
    public ResponseEntity<Void> enterRoom(@PathVariable("roomId") UUID roomId, @RequestParam("nickname") String nickname) {
        chatRoomMembers.computeIfAbsent(roomId, k -> new HashSet<>()).add(nickname);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/{roomId}/leave")
    @ResponseBody
    public ResponseEntity<Void> leaveRoom(@PathVariable("roomId") UUID roomId, @RequestParam("nickname") String nickname) {
        Set<String> members = chatRoomMembers.get(roomId);
        if (members != null) {
            members.remove(nickname);
            if (members.isEmpty()) {
                chatRoomMembers.remove(roomId);
            }
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/room/{roomId}/members")
    @ResponseBody
    public ResponseEntity<List<Member>> getRoomMembers(@PathVariable("roomId") UUID roomId) {
        Set<String> memberNicknames = chatRoomMembers.getOrDefault(roomId, Collections.emptySet());
        List<Member> members = memberNicknames.stream()
                .map(nickname -> memberRepository.findByNickname(nickname).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }



}