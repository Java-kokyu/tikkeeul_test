package com.hanghae0705.sbmoney.model.domain.statistic;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@RequiredArgsConstructor
public class StatisticsMyDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long categoryId;

    @NotNull
    private String standardDate;

    @NotNull
    private String itemName;

    @NotNull
    private int totalPrice;

    @NotNull
    private Long totalCount;

    private int rankPrice;
    private int rankCount;

    @Builder
    public StatisticsMyDay(Long userId, Long categoryId, String standardDate, String itemName, int totalPrice, Long totalCount, int rankPrice, int rankCount) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.standardDate = standardDate;
        this.itemName = itemName;
        this.totalPrice = totalPrice;
        this.totalCount = totalCount;
        this.rankPrice = rankPrice;
        this.rankCount = rankCount;
    }

    public void changeRankCount(int rank){
        this.rankCount = rank;
    }

    @Getter
    public static class MyDailyByPrice {
        private Long userId;
        private Long categoryId;
        private String itemName;
        private int rankPrice;

        @Builder
        public MyDailyByPrice(Long userId, String itemName, Long categoryId, int rankPrice) {
            this.userId = userId;
            this.itemName = itemName;
            this.categoryId = categoryId;
            this.rankPrice = rankPrice;
        }
    }

    @Getter
    public static class MyDailyByCount {
        private Long userId;
        private Long categoryId;
        private String itemName;
        private int rankCount;

        @Builder
        public MyDailyByCount(Long userId, String itemName, Long categoryId, int rankCount) {
            this.userId = userId;
            this.itemName = itemName;
            this.categoryId = categoryId;
            this.rankCount = rankCount;
        }
    }
}
