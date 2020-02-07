package com.bv.actionmonitor.message;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@ToString
@Builder
@Data
public class UserMessage {
    private String sender;
    private String content;
    private Date date;
}
