package com.hanghae0705.sbmoney.controller.chat;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.model.domain.chat.entity.ChatRoom;
import com.hanghae0705.sbmoney.model.domain.chat.ChatRoomProsCons;
import com.hanghae0705.sbmoney.repository.chat.ChatRoomRepository;
import com.hanghae0705.sbmoney.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    // 전체 채팅방 조회
    @GetMapping("/api/chat/rooms")
    public ResponseEntity<Message> getRooms() {
        return ResponseEntity.ok(chatService.getRooms());
    }

    // 채팅방 상세 조회
    @GetMapping("/api/chat/room/{roomId}")
    public Message roomInfo(@PathVariable String roomId) throws IOException {
        return chatService.getRoomDetail(roomId);
    }

    // 채팅방 생성
    @PostMapping("/api/chat/room")
    public Message createRoom(@RequestBody ChatRoom.Request request) {
        return chatService.createRoom(request);
    }

    // 채팅방 삭제
    @DeleteMapping("/api/chat/room/{roomId}") //로그랑 메세지도 삭제
    public Message deleteRoom(@PathVariable String roomId){
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId).orElseThrow(
                () -> new IllegalArgumentException("존재하는 방이 없습니다.")
        );
        chatRoomRepository.delete(chatRoom);
        return Message.builder()
                .result(true)
                .respMsg("채팅방이 삭제되었습니다.")
                .build();
    }

    // 투표
    @Transactional
    @PostMapping("/api/chat/room/{roomId}/vote")
    public Message vote(@PathVariable String roomId, @RequestBody ChatRoomProsCons.Request chatRoomProsConsRequest){
        return chatService.vote(roomId, chatRoomProsConsRequest);
    }

    // userCount가 높은 상위 5개 방을 호출
    @GetMapping("/api/chat/room/top")
    public ResponseEntity<Message> getTopRoom() {
        return ResponseEntity.ok(chatService.getTopRoom());
    }

    // 채팅 기록 저장
    @GetMapping("/api/chat/room/{roomId}/save")
    public ResponseEntity<Message> saveChatLog(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.saveChatLog(roomId));
    }

    // 종료된 채팅방 목록 조회
    @GetMapping("/api/closedChat/rooms")
    public ResponseEntity<Message> getClosedRoom() {
        return ResponseEntity.ok(chatService.getClosedRooms());
    }

    // 종료된 채팅방 상세 조회
    @GetMapping("/api/closedChat/room/{closedRoomId}")
    public ResponseEntity<Message> getClosedRoomDetail(@PathVariable String closedRoomId) {
        return ResponseEntity.ok(chatService.getCloesdChatRoom(closedRoomId));
    }
}
