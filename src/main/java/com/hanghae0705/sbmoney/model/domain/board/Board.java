package com.hanghae0705.sbmoney.model.domain.board;

import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.model.domain.baseEntity.BaseEntity;
import com.hanghae0705.sbmoney.model.domain.item.GoalItem;
import com.hanghae0705.sbmoney.model.domain.item.SavedItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
public class Board extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @NotNull
    private String title;

    @NotNull
    private String contents;

    @Column
    private String image;

    @NotNull
    private Long goalItemId;

    @NotNull
    private Long categoryId;

    @NotNull
    private String categoryName;


    @NotNull
    private String goalItemName;

    @NotNull
    private int goalItemCount;

    @NotNull
    private int goalItemPrice;

    @NotNull
    private double goalPercent;

    @NotNull
    private Long commentCount;

    @NotNull
    private Long likeCount;

    @NotNull
    private Long viewCount;

    @NotNull
    private boolean checkLike;

    public Board(Request request, GoalItem goalItem, Optional<User> user) {
        this.categoryId = goalItem.getItem().getCategory().getId();
        this.categoryName = goalItem.getItem().getCategory().getName();
        this.goalItemId = goalItem.getId();
        this.goalItemName = goalItem.getItem().getName();
        this.goalPercent = goalItem.getGoalPercent();
        this.goalItemCount = goalItem.getCount();
        this.goalItemPrice = goalItem.getItem().getDefaultPrice();
        this.image = goalItem.getImage();
        this.title = request.title;
        this.contents = request.contents;
        this.user = user.get();
        this.likeCount = (long) 0;
        this.checkLike = false;
        this.commentCount = (long) 0;
        this.viewCount = (long) 0;
    }

    public void updateBoard(Board.Update update) {
        this.contents = update.contents;
    }
    public void updateUserNull() {
        this.user = null;
    }

    public void likeBoard(boolean like, Long likeCount) {

        this.checkLike = like;
        this.likeCount = likeCount;
    }

    public void commentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public void viewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public void changeImage(String image) {
        this.image = image;
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        private String title;
        private String contents;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update {
        private String title;
        private String contents;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private Long boardId;
        private String UserId;
        private String nickname;
        private String profileImg;
        private String title;
        private String contents;
        private String image;
        private Long categoryId;
        private String categoryName;
        private Long goalItemId;
        private String goalItemName;
        private double goalPercent;
        private Long likeCount;
        private Long viewCount;
        private Long commentCount;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private boolean checkLike;

        public Response(Board board) {
            this.boardId = board.getId();
            this.UserId = board.getUser().getUsername();
            this.nickname = board.getUser().getNickname();
            this.profileImg = board.getUser().getProfileImg();
            this.title = board.getTitle();
            this.contents = board.getContents();
            this.image = board.getImage();
            this.categoryId = board.getCategoryId();
            this.categoryName = board.getCategoryName();
            this.goalItemId = board.getGoalItemId();
            this.goalItemName = board.getGoalItemName();
            this.goalPercent = board.getGoalPercent();
            this.commentCount = board.getCommentCount();
            this.viewCount = board.getViewCount();
            this.likeCount = board.getLikeCount();
            this.createdAt = board.getCreatedDate();
            this.modifiedAt = board.getModifiedDate();
            this.likeCount = board.getLikeCount();
            this.checkLike = board.isCheckLike();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class SaveItemResponse {
        private Long boardId;
        private String userId;
        private int price;
        private int totalPrice;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private int savedItemTotalPrice;
        private List<Board.SaveItem> savedItemList;

        public SaveItemResponse(Board board, List<Board.SaveItem> saveItemList, int savedItemTotalPrice) {
            this.boardId = board.getId();
            this.userId = board.getUser().getUsername();
            this.totalPrice = board.goalItemPrice * board.goalItemCount;
            this.price = board.goalItemPrice;
            this.createdAt = board.getCreatedDate();
            this.modifiedAt = board.getModifiedDate();
            this.savedItemList = saveItemList;
            this.savedItemTotalPrice = savedItemTotalPrice;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class SaveItem {
        private Long saveItemId;
        private String saveItemName;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;
        private int price;
        private Long categoryId;
        private String categoryName;

        public SaveItem(SavedItem savedItem) {
            this.saveItemId = savedItem.getItem().getId();
            this.saveItemName = savedItem.getItem().getName();
            this.price = savedItem.getPrice();
            this.categoryId = savedItem.getItem().getCategory().getId();
            this.categoryName = savedItem.getItem().getCategory().getName();
            this.createdDate = savedItem.getCreatedDate();
            this.modifiedDate = savedItem.getModifiedDate();
        }
    }
}
