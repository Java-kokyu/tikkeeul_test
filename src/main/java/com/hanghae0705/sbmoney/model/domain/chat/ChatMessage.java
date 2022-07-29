package com.hanghae0705.sbmoney.model.domain.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
//@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
public class ChatMessage {

    // 메시지 타입 : 입장, 퇴장, 채팅
    public enum MessageType {
        @Enumerated(value = EnumType.STRING)
        ENTER,
        @Enumerated(value = EnumType.STRING)
        QUIT,
        @Enumerated(value = EnumType.STRING)
        TALK
    }
    private MessageType type; // 메시지 타입
    private String roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private String profileImg; // 메시지 보낸사람 프로필
    private String message; // 메시지
    private Long userCount; // 채팅방 조회수
    private int timeLimit; // 채팅방 시간제한
    private long leftTime; // 입장 시 남은 시간

    public ChatMessage() {
    }

    @Builder
    public ChatMessage(MessageType type, String roomId, String sender, String profileImg, String message, Long userCount, int timeLimit, long leftTime) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.profileImg = profileImg;
        this.message = message;
        this.userCount = userCount;
        this.timeLimit = timeLimit;
        this.leftTime = leftTime;
    }
}
