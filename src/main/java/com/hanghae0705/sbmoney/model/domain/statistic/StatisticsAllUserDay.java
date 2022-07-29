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
public class StatisticsAllUserDay {

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
    public StatisticsAllUserDay( Long categoryId, String standardDate, String itemName, int totalPrice, Long totalCount, int rankPrice, int rankCount) {
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
    public static class AllUserDailyByPrice {

        private String itemName;
        private final Long categoryId;
        private int rankPrice;

        @Builder
        public AllUserDailyByPrice( String itemName, Long categoryId, int rankPrice) {

            this.itemName = itemName;
            this.categoryId = categoryId;
            this.rankPrice = rankPrice;
        }
    }

    @Getter
    public static class AllUserDailyByCount {

        private String itemName;
        private final Long categoryId;
        private int rankCount;

        @Builder
        public AllUserDailyByCount( String itemName, Long categoryId, int rankCount) {
            this.itemName = itemName;
            this.categoryId = categoryId;
            this.rankCount = rankCount;
        }
    }
}


