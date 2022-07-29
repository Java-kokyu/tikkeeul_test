package com.hanghae0705.sbmoney.service;


import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.model.domain.chat.ChatRoomProsCons;
import com.hanghae0705.sbmoney.model.domain.chat.RedisChatRoom;
import com.hanghae0705.sbmoney.model.domain.chat.entity.ChatLog;
import com.hanghae0705.sbmoney.model.domain.chat.ChatMessage;
import com.hanghae0705.sbmoney.model.domain.chat.entity.ChatRoom;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.repository.chat.ChatLogRepository;
import com.hanghae0705.sbmoney.repository.chat.ChatRoomProsConsRepository;
import com.hanghae0705.sbmoney.repository.chat.ChatRoomRepository;
import com.hanghae0705.sbmoney.repository.chat.RedisChatRoomRepository;
import com.hanghae0705.sbmoney.validator.ChatRoomValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, ChatMessage> redisChatMessageTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomProsConsRepository chatRoomProsConsRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;
    private final ChatLogRepository chatLogRepository;
    private final CommonService commonService;
    private final ChatRoomValidator chatRoomValidator;

    /**
     * destination정보에서 roomId 추출
     */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessage chatMessage) {
        chatMessage.setUserCount(redisChatRoomRepository.getUserCount(chatMessage.getRoomId()));
        chatMessage.setTimeLimit(redisChatRoomRepository
                .findRoomById(chatMessage.getRoomId()).getTimeLimit());

        // 채팅방에서 남은 시간을 계산해 반환한다.
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(chatMessage.getRoomId()).orElseThrow(() -> new IllegalArgumentException("해당하는 방이 없습니다."));
        long betweenSeconds = Duration.between(chatRoom.getCreatedDate(), LocalDateTime.now()).getSeconds();
        long leftTime = (chatRoom.getTimeLimit() * 60L) - betweenSeconds;
        chatMessage.setLeftTime(leftTime);

        log.info("CHAT {}, {}", redisChatRoomRepository.findRoomById(chatMessage.getRoomId()), leftTime);

        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
            chatMessage.setSender("[알림]");
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
            chatMessage.setSender("[알림]");
        }

        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

    // 전체 채팅 목록 조회
    public Message getRooms() {
        Long userId = commonService.getUserId();
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByProceedingOrderByCreatedAtDesc(true);
        List<ChatRoom.Response> chatRoomResponseList = new ArrayList<>();
        //proceeding(true/false)
        for (ChatRoom chatRoom : chatRooms) {
            Long userCount = redisChatRoomRepository.getUserCount(chatRoom.getRoomId());
            List<ChatRoomProsCons> chatRoomProsConsList = chatRoom.getChatRoomProsConsList();
            Boolean checkProsCons = null;
            //찬성 반대를 눌렀는 지 체크
            if (!chatRoomProsConsList.isEmpty()) {
                for (ChatRoomProsCons chatRoomProsCons : chatRoomProsConsList) {
                    if (chatRoomProsCons.getUserId().equals(userId)) {
                        checkProsCons = chatRoomProsCons.getProsCons();
                        break;
                    }
                }
            }

            // 남은 시간 계산하여 반환
            long betweenSeconds = Duration.between(chatRoom.getCreatedDate(), LocalDateTime.now()).getSeconds();
            long leftTime = (chatRoom.getTimeLimit() * 60L) - betweenSeconds;

            chatRoomResponseList.add(new ChatRoom.Response(chatRoom, checkProsCons, userCount, leftTime));
        }
        return Message.builder()
                .result(true)
                .respMsg("채팅방 전체 조회에 성공했습니다.")
                .data(chatRoomResponseList)
                .build();
    }

    // 채팅방 상세 조회
    public Message getRoomDetail(String roomId) {
        ChatRoom chatRoom = chatRoomValidator.isValidChatRoom(roomId);
        Long userCount = redisChatRoomRepository.getUserCount(roomId);
        return Message.builder()
                .result(true)
                .respMsg("채팅방 상세 조회에 성공했습니다.")
                .data(new ChatRoom.Response(chatRoom, userCount))
                .build();
    }

    // 채팅방 생성
    public Message createRoom(ChatRoom.Request request) {
        User user = commonService.getUser();
        String RoomUuid = UUID.randomUUID().toString();
        ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom(user, request.getTimeLimit(), request.getComment(), RoomUuid, true));
        String redisChatRoomId = chatRoom.getRoomId();
        redisChatRoomRepository.createChatRoom(redisChatRoomId, request.getComment(), request.getTimeLimit());
        return Message.builder()
                .result(true)
                .respMsg("방 개설을 성공하였습니다.")
                .data(new ChatRoom.Response(chatRoom, 0L))
                .build();
    }

    // 투표
    public Message vote(String roomId, ChatRoomProsCons.Request chatRoomProsConsRequest) {
        Long userId = commonService.getUserId();
        ChatRoom chatRoom = chatRoomValidator.isValidChatRoom(roomId);
        Boolean prosCons = chatRoomProsConsRequest.getProsCons();
        ChatRoomProsCons checkVote = chatRoomProsConsRepository.findByUserIdAndChatRoom(userId, chatRoom);
        if (checkVote != null) {
            if (prosCons != checkVote.getProsCons()) {
                chatRoom.MinusVoteCount(!prosCons);
                chatRoom.PlusVoteCount(prosCons);
                checkVote.update(chatRoomProsConsRequest.getProsCons());
            }
        } else {
            ChatRoomProsCons chatRoomProsCons = new ChatRoomProsCons(chatRoomProsConsRequest.getProsCons(), userId, chatRoom);
            chatRoomProsConsRepository.save(chatRoomProsCons);
            chatRoom.PlusVoteCount(prosCons);
        }
        return Message.builder()
                .result(true)
                .respMsg("쓸까? 말까? 투표를 하셨습니다.")
                .data(new ChatRoom.VoteResponse(chatRoom))
                .build();

    }

    /**
     * 채팅 종료 시 채팅 기록 저장
     */
    @Transactional
    public Message saveChatLog(String roomId) {
        //redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        RedisOperations<String, ChatMessage> operations = redisChatMessageTemplate.opsForList().getOperations();

        ChatRoom chatRoom = chatRoomValidator.isValidChatRoom(roomId);

        // proceeding 종료방(false)으로 변경
        chatRoom.changeProceeding(false);

        // roomId에 해당하는 ChatMessage를 찾아서 ChatLog에 저장
        List<ChatMessage> chatMessageList = operations.opsForList().range(roomId, 0, -1);

        for (ChatMessage chatMessage : chatMessageList) {
            ChatLog chatLog = ChatLog.builder()
                    .id(null)
                    .type(chatMessage.getType())
                    .profileImg(chatMessage.getProfileImg())
                    .nickname(chatMessage.getSender())
                    .message(chatMessage.getMessage())
                    .chatRoom(chatRoom)
                    .build();
            chatLogRepository.save(chatLog);
        }

        return Message.builder()
                .result(true)
                .respMsg("채팅 기록 저장에 성공했습니다.")
                .data(chatMessageList)
                .build();
    }

    /**
     * userCount 기준 상위 5개 추출
     */
    public Message getTopRoom() {

        // 모든 roomId 호출
        List<RedisChatRoom> allRooms = redisChatRoomRepository.findAllRoom();
        for (RedisChatRoom room : allRooms) {
            Long userCount = redisChatRoomRepository.getUserCount(room.getRoomId());
            room.setUserCount(userCount);
        }

        // roomId에 해당하는 userCount 찾기
        Map<String, Long> roomMap = allRooms.stream()
                .collect(Collectors.toMap(
                        RedisChatRoom::getRoomId,
                        RedisChatRoom::getUserCount
                ));

        // userCount 순으로 정렬하기
        Map<String, Long> topRoom = roomMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                //.limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new)
                );

        // topRoom 중, proceeding이 true인 것만 DB에서 chatRoom 데이터 읽어오기
        List<ChatRoom.Response> chatRoomList = topRoom.keySet().stream()
                .map(chatRoomId -> chatRoomRepository.findByRoomIdAndProceeding(chatRoomId, true))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(room ->
                        ChatRoom.Response.builder()
                                .chatRoom(room)
                                .userCount(topRoom.get(room.getRoomId()))
                                .leftTime((room.getTimeLimit() * 60L) - Duration.between(room.getCreatedDate(), LocalDateTime.now()).getSeconds())
                                .build()
                )
                .collect(Collectors.toList());

        return Message.builder()
                .result(true)
                .respMsg("상위 5개 결과를 조회하였습니다.")
                .data(chatRoomList)
                .build();
    }

    // 종료 채팅 목록 조회
    public Message getClosedRooms() {
        List<ChatRoom> closedChatRooms = chatRoomRepository.findAllByProceeding(false);
        List<ChatRoom.ClosedResponse> chatRoomResponseList = new ArrayList<>();
        Message message;

        if(closedChatRooms.isEmpty()) {
            message = Message.builder()
                    .result(false)
                    .respMsg("종료된 채팅방이 없습니다.")
                    .build();
        } else {
            for (ChatRoom chatRoom : closedChatRooms) {
                if (chatRoom.getUser() == null) {
                    chatRoom.changeUser(User.builder()
                            .nickname("탈퇴회원")
                            .profileImg("none")
                            .build());
                }
                chatRoomResponseList.add(new ChatRoom.ClosedResponse(chatRoom));
            }

            message = Message.builder()
                    .result(true)
                    .respMsg("종료방 상세 전체 조회에 성공했습니다.")
                    .data(chatRoomResponseList)
                    .build();
        }
        return message;
    }

    // 종료 채팅 상세 조회
    public Message getCloesdChatRoom(String closedRoomId) {
        // 챗룸 정보(닉네임, 프로필 정보, 코멘트, 찬/반 비율, 챗로그) 가져오기
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(closedRoomId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 방입니다.")
        );

        float totalCount = chatRoom.getVoteTrueCount() + chatRoom.getVoteFalseCount();
        int voteTruePercent = Math.round(chatRoom.getVoteTrueCount() / totalCount * 100);
        int voteFalsePercent = Math.round(chatRoom.getVoteFalseCount() / totalCount * 100);

        ChatRoom.ClosedRoomDetail closedRoomDetail = ChatRoom.ClosedRoomDetail.builder()
                .closedRoomId(chatRoom.getRoomId())
                .authorNickname(chatRoom.getUser().getNickname())
                .authorProfileImg(chatRoom.getUser().getProfileImg())
                .comment(chatRoom.getComment())
                .voteTruePercent(voteTruePercent)
                .voteFalsePercent(voteFalsePercent)
                .chatLogList(chatRoom.getChatLogList())
                .build();

        return Message.builder()
                .result(true)
                .respMsg("종료방 상세 조회에 성공했습니다.")
                .data(closedRoomDetail)
                .build();
    }

}
