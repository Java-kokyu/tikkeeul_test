package com.hanghae0705.sbmoney.model.domain.chat;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalTime;


@Getter
@Setter
public class RedisChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977289006639L;

    private String roomId;
    private String comment;
    private Long userCount; // 채팅방 인원수
    private int timeLimit;

    public static RedisChatRoom create(String uuid, String comment, int timeLimit) {
        RedisChatRoom redisChatRoom = new RedisChatRoom();
        redisChatRoom.roomId = uuid;
        redisChatRoom.comment = comment;
        redisChatRoom.userCount = 0L;
        redisChatRoom.timeLimit = timeLimit;
        return redisChatRoom;
    }
}
