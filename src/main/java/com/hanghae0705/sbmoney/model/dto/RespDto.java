package com.hanghae0705.sbmoney.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RespDto {
    private boolean result;
    private String respMsg;

    @Builder
    public RespDto(boolean result, String respMsg) {
        this.result = result;
        this.respMsg = respMsg;
    }
}
