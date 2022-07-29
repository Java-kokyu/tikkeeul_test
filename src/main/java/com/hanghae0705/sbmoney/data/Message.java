package com.hanghae0705.sbmoney.data;

import lombok.Builder;
import lombok.Data;

@Data
public class Message {

    private boolean result;
    private String respMsg;
    private Object data;

    @Builder
    public Message(boolean result, String respMsg, Object data) {
        this.result = result;
        this.respMsg = respMsg;
        this.data = data;
    }

    public Message(boolean result, String respMsg) {
        this.result = result;
        this.respMsg = respMsg;
    }
}


