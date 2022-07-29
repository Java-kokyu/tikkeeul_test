package com.hanghae0705.sbmoney.model.domain.board;

import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.model.domain.baseEntity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private User user;

    @Column(nullable = false, length = 200)
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="BOARD_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private Board board;
        private User user;
        private String comment;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String username;
        private String profileImg;
        private Long commentId;
        private String comment;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public Response(Comment comment){
            this.username = comment.getUser().getUsername();
            this.profileImg = comment.getUser().getProfileImg();
            this.commentId = comment.getId();
            this.comment = comment.getComment();
            this.createdAt = comment.getCreatedDate();
            this.modifiedAt = comment.getModifiedDate();
        }
    }

    @Builder
    public Comment(Request request, Board board, User user){
        this.comment = request.getComment();
        this.board = board;
        this.user = user;
    }

    // 댓글 수정
    public void updateComment(Request request) {
        this.comment = request.getComment();
    }

    public void updateUserNull() {
        this.user = null;
    }
}