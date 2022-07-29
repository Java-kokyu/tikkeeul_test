package com.hanghae0705.sbmoney.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoalItemForStatisticsAllUserDto {
    private Long categoryId;
    private String itemName;
    private int totalPrice;
    private Long totalCount;
}
