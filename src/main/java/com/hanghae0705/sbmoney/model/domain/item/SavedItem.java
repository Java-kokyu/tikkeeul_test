package com.hanghae0705.sbmoney.model.domain.item;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hanghae0705.sbmoney.model.domain.user.Favorite;
import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.model.domain.baseEntity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@RequiredArgsConstructor
public class SavedItem extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @JsonBackReference(value = "user-fk")
    private User user;

    @Column(nullable = false)
    private int price;

    @OneToOne
    @JoinColumn(name = "ITEM_ID")
    @JsonBackReference(value = "item-fk")
    private Item item;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "GOAL_ITEM_ID")
    @JsonBackReference(value = "goalItem-fk")
    private GoalItem goalItem;

    public SavedItem(Item item, int price, User user, GoalItem goalItem){
        this.item = item;
        this.price = price;
        this.user = user;
        this.goalItem = goalItem;
    }


    public void update(int price){
        this.price = price;
    }

    public void setGoalItem(GoalItem goalItem){
        this.goalItem = goalItem;
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private Long itemId;
        private Long goalItemId;
        private int price;
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update {
        private int price;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private LocalDateTime modifiedDate;
        private Long savedItemId;
        private Long categoryId;
        private String categoryName;
        private Long itemId;
        private String itemName;
        private int price;
        private Long favoriteId;
        private Boolean favorite;

        public Response(SavedItem savedItem, Favorite.SavedItemResponse savedItemResponse) {
            this.modifiedDate = savedItem.getModifiedDate();
            this.savedItemId = savedItem.getId();
            this.categoryId = savedItem.getItem().getCategory().getId();
            this.categoryName = savedItem.getItem().getCategory().getName();
            this.itemId = savedItem.getItem().getId();
            this.itemName = savedItem.getItem().getName();
            this.price = savedItem.getPrice();
            this.favoriteId = savedItemResponse.getId();
            this.favorite = savedItemResponse.getFavorite();
        }
    }

}
