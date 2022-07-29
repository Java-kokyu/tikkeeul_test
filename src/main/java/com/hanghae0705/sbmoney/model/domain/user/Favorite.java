package com.hanghae0705.sbmoney.model.domain.user;

import com.hanghae0705.sbmoney.model.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Favorite {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private User user;

    @OneToOne
    @JoinColumn(name="ITEM_ID")
    private Item item;

    @Column(nullable = false)
    private int price;

    public Favorite(Item.Request request, User user, Item item) {
        this.price = request.getDefaultPrice();
        this.user = user;
        this.item = item;
    }

    public Favorite(Request request, User user, Item item) {
        this.item = item;
        this.price = request.getPrice();
        this.user = user;
    }

    public void updateFavorite(UpdateFavorite request) {
        this.price = request.getPrice();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateFavorite {
        private int price;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String itemName;
        private Long itemId;
        private int price;
        private Long categoryId;
    }

    @Getter
    @NoArgsConstructor
    @Setter
    public static class SavedItemResponse {
        private Long id;
        private Boolean favorite;
    }

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Long favoriteItemId;
        private Long itemId;
        private String itemName;
        private int price;
        private Long categoryId;
        private String categoryName;

        public Response(Favorite favorite){
            this.favoriteItemId = favorite.getId();
            Item item = favorite.getItem();
            this.itemId = favorite.getItem().getId();
            this.itemName = favorite.getItem().getName();
            this.price = favorite.getPrice();
            this.categoryId = item.getCategory().getId();
            this.categoryName = item.getCategory().getName();
        }
    }
}
