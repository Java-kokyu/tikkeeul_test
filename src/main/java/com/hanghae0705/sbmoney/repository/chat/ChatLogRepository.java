package com.hanghae0705.sbmoney.repository.chat;

import com.hanghae0705.sbmoney.model.domain.chat.entity.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

    List<ChatLog> findChatLogByChatRoomId(Long closedRoomId);

}
