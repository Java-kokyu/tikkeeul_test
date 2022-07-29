package com.hanghae0705.sbmoney.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SavedItemForStatisticsDto { // repository에서 static 메소드를 참조할 수 없어 Dto 별도로 생성함
    private Long userId;
    private Long categoryId;
    private String itemName;
    private int totalPrice;
    private Long totalCount;
    private String username;
}
