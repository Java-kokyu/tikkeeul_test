package com.hanghae0705.sbmoney.validator;

import com.hanghae0705.sbmoney.exception.ApiRequestException;
import com.hanghae0705.sbmoney.model.domain.chat.entity.ChatRoom;
import com.hanghae0705.sbmoney.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.hanghae0705.sbmoney.exception.ApiException.NO_CHAT_ROOM;

@Component
@RequiredArgsConstructor
public class ChatRoomValidator {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom isValidChatRoom(String roomId) {
        return chatRoomRepository.findByRoomId(roomId).orElseThrow(
                () -> new ApiRequestException(NO_CHAT_ROOM)
        );
    }
}
