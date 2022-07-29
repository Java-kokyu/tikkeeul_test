package com.hanghae0705.sbmoney.model.domain.statistic;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@RequiredArgsConstructor
public class StatisticsAllUserMonth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String standardDate;

    @NotNull
    private String itemName;

    @NotNull
    private int totalPrice;

    @NotNull
    private Long totalCount;

    @NotNull
    private Long categoryId;

    private int rankPrice;
    private int rankCount;

    @Builder
    public StatisticsAllUserMonth(String standardDate, String itemName, Long categoryId, int totalPrice, Long totalCount, int rankPrice, int rankCount) {
        this.standardDate = standardDate;
        this.itemName = itemName;
        this.categoryId = categoryId;
        this.totalPrice = totalPrice;
        this.totalCount = totalCount;
        this.rankPrice = rankPrice;
        this.rankCount = rankCount;
    }

    public void changeRankCount(int rank){
        this.rankCount = rank;
    }

    @Getter
    public static class AllMonthlyByPrice {
        private final int rankPrice;
        private final Long categoryId;
        private final String itemName;

        @Builder
        public AllMonthlyByPrice(int rankPrice, Long categoryId, String itemName) {
            this.rankPrice = rankPrice;
            this.categoryId = categoryId;
            this.itemName = itemName;
        }
    }

    @Getter
    public static class AllMonthlyByCount {
        private final int rankCount;
        private final Long categoryId;
        private final String itemName;

        @Builder
        public AllMonthlyByCount(int rankCount, Long categoryId, String itemName) {
            this.rankCount = rankCount;
            this.categoryId = categoryId;
            this.itemName = itemName;
        }
    }
}
