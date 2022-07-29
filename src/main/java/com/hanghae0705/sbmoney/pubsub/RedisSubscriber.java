package com.hanghae0705.sbmoney.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae0705.sbmoney.model.domain.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.ZonedDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    private final RedisTemplate<String, ChatMessage> redisTemplate;

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 Redis Subscriber가 해당 메시지를 받아 처리한다.
     */
    public void sendMessage(String publishMessage) {
        try {
            //redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
            // ChatMessage 객채로 맵핑
            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);

            // 기존 메시지 List에 넣기 - Redis에 넣을 때는 serialize가 필요함. 반대로 조회할 때는 deserialize
            redisTemplate.opsForList().rightPush(chatMessage.getRoomId(), chatMessage);

            // 최대 시간 설정(15분)
            redisTemplate.expireAt(chatMessage.getRoomId(), Date.from(ZonedDateTime.now().plusMinutes(15).toInstant()));
            RedisOperations<String, ChatMessage> operations = redisTemplate.opsForList().getOperations();

            // 최초 진입 시 기존 채팅 기록 출력
//            if (chatMessage.getType().equals(ChatMessage.MessageType.ENTER)) {
//                messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(),
//                        operations.opsForList().range(chatMessage.getRoomId(), 0, -1));
//            }

            // 채팅방을 구독한 클라이언트에게 메시지 발송
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);

        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
