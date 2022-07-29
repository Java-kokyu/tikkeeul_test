package com.hanghae0705.sbmoney.model.domain.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hanghae0705.sbmoney.model.domain.baseEntity.CreatedTime;
import com.hanghae0705.sbmoney.model.domain.chat.ChatRoomProsCons;
import com.hanghae0705.sbmoney.model.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class ChatRoom extends CreatedTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomId;

    @Column(nullable = false)
    private Boolean proceeding;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private int timeLimit;

    private int voteTrueCount;
    private int voteFalseCount;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "chatRoom-fk")
    private List<ChatRoomProsCons> chatRoomProsConsList;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "chatLog-chatRoom-fk")
    private List<ChatLog> chatLogList;

    public ChatRoom(User user, int timeLimit, String comment, String roomId, Boolean proceeding) {
        this.user = user;
        this.timeLimit = timeLimit;
        this.comment = comment;
        this.roomId = roomId;
        this.proceeding = true;
    }

    public ChatRoom(User user, String roomId) {
        this.user = user;
        this.roomId = roomId;
    }

    public void PlusVoteCount(Boolean prosCons) {
        if (prosCons) {
            this.voteTrueCount++;
        } else {
            this.voteFalseCount++;
        }
    }

    public void MinusVoteCount(Boolean prosCons) {
        if (prosCons) {
            this.voteTrueCount--;
        } else {
            this.voteFalseCount--;
        }
    }

    public void changeProceeding(Boolean proceeding) {
        this.proceeding = proceeding;
    }
    public void changeUser(User user) {
        this.user = user;
    }

    public void updateUserNull() {
        this.user = null;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private int timeLimit;
        private String comment;
    }

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String roomId;
        private int timeLimit;
        private String comment;
        private String authorNickname;
        private String authorProfileImg;
        private Long userCount;
        private Boolean prosCons;
        private LocalDateTime createdAt;
        private Long leftTime;

        @Builder
        public Response(ChatRoom chatRoom, Long userCount, Long leftTime) {
            this.roomId = chatRoom.getRoomId();
            this.timeLimit = chatRoom.getTimeLimit();
            this.comment = chatRoom.getComment();
            this.authorNickname = chatRoom.getUser().getNickname();
            this.authorProfileImg = chatRoom.getUser().getProfileImg();
            this.createdAt = chatRoom.getCreatedDate();
            this.userCount = userCount;
            this.leftTime = leftTime;
        }
        @Builder
        public Response(ChatRoom chatRoom, Boolean chatRoomProsCons, Long userCount, Long leftTime) {
            this.roomId = chatRoom.getRoomId();
            this.timeLimit = chatRoom.getTimeLimit();
            this.comment = chatRoom.getComment();
            this.authorNickname = chatRoom.getUser().getNickname();
            this.authorProfileImg = chatRoom.getUser().getProfileImg();
            this.userCount = userCount;
            this.createdAt = chatRoom.getCreatedDate();
            this.prosCons = chatRoomProsCons;
            this.leftTime = leftTime;
        }


        public Response(ChatRoom chatRoom, Long userCount) {
            this.roomId = chatRoom.getRoomId();
            this.timeLimit = chatRoom.getTimeLimit();
            this.comment = chatRoom.getComment();
            this.authorNickname = chatRoom.getUser().getNickname();
            this.authorProfileImg = chatRoom.getUser().getProfileImg();
            this.userCount = userCount;
            this.createdAt = chatRoom.getCreatedDate();
            this.userCount = userCount;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class ClosedResponse {
        private String roomId;
        private String comment;
        private String authorNickname;
        private String authorProfileImg;
        private int voteTruePercent;
        private int voteFalsePercent;
        private LocalDateTime createdAt;

        @Builder
        public ClosedResponse(ChatRoom chatRoom) {
            int totalCount = chatRoom.getVoteTrueCount() + chatRoom.getVoteFalseCount();
            this.roomId = chatRoom.getRoomId();
            this.comment = chatRoom.getComment();
            this.authorNickname = chatRoom.getUser().getNickname();
            this.authorProfileImg = chatRoom.getUser().getProfileImg();
            this.voteTruePercent = (totalCount == 0) ? 0 : chatRoom.getVoteTrueCount() / totalCount * 100;
            this.voteFalsePercent = (totalCount == 0) ? 0 : chatRoom.getVoteFalseCount() / totalCount * 100;
            this.createdAt = chatRoom.getCreatedDate();
        }
    }


    @Getter
    @RequiredArgsConstructor
    public static class VoteResponse {
        private String roomId;
        private String comment;
        private String authorNickname;
        private String authorProfileImg;
        private int voteTrueCount;
        private int voteFalseCount;
        private LocalDateTime createdAt;

        @Builder
        public VoteResponse(ChatRoom chatRoom) {
            this.roomId = chatRoom.getRoomId();
            this.comment = chatRoom.getComment();
            this.authorNickname = chatRoom.getUser().getNickname();
            this.authorProfileImg = chatRoom.getUser().getProfileImg();
            this.voteTrueCount = chatRoom.getVoteTrueCount();
            this.voteFalseCount = chatRoom.getVoteFalseCount();
            this.createdAt = chatRoom.getCreatedDate();
        }
    }


    @Getter
    @NoArgsConstructor
    public static class ClosedRoomDetail {
        private String closedRoomId;
        private String authorNickname;
        private String authorProfileImg;
        private String comment;
        private float voteTruePercent;
        private float voteFalsePercent;
        private List<ChatLog> chatLogList;

        @Builder
        public ClosedRoomDetail(String closedRoomId, String authorNickname, String authorProfileImg, String comment, float voteTruePercent, float voteFalsePercent, List<ChatLog> chatLogList) {
            this.closedRoomId = closedRoomId;
            this.authorNickname = authorNickname;
            this.authorProfileImg = authorProfileImg;
            this.comment = comment;
            this.voteTruePercent = voteTruePercent;
            this.voteFalsePercent = voteFalsePercent;
            this.chatLogList = chatLogList;
        }
    }


}
