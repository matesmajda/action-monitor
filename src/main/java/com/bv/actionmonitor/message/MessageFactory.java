package com.bv.actionmonitor.message;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
class MessageFactory {

    MessageEntity createSanitizedMessage(String sender, String recipient, String content) {
        sender = StringEscapeUtils.escapeHtml4(sender);
        recipient = StringEscapeUtils.escapeHtml4(recipient);
        content = StringEscapeUtils.escapeHtml4(content);
        Date now = new Date();
        return MessageEntity.builder().sender(sender).recipient(recipient).content(content).createdAt(now).build();
    }
}
