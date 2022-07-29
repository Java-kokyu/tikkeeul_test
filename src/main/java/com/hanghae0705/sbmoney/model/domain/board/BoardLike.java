package com.hanghae0705.sbmoney.model.domain.board;

import com.hanghae0705.sbmoney.model.domain.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Getter
@RequiredArgsConstructor
public class BoardLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private boolean like;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "BOARD_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    public BoardLike(Board board, User user) {
        this.like = true;
        this.user = user;
        this.board = board;
    }

    public void changeLike(boolean like) {
        this.like = like;
    }


    @Getter
    public static class Response {
        private Long likeCount;
        private boolean checkLike;

        public Response(Long likeCount, boolean checkLike) {
            this.checkLike = checkLike;
            this.likeCount = likeCount;
        }
    }
}
