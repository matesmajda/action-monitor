package com.bv.actionmonitor.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageFactoryTest {

    @Test
    void factorySetsFields() {
        MessageFactory factory = new MessageFactory();

        String sender = "sender";
        String recipient = "recipient";
        String content = "content";

        MessageEntity message = factory.createSanitizedMessage(sender, recipient, content);

        assertEquals(sender, message.getSender());
        assertEquals(recipient, message.getRecipient());
        assertEquals(content, message.getContent());
        assertNotNull(message.getCreatedAt());
    }

    @Test
    void factoryEscapesStrings() {
        MessageFactory factory = new MessageFactory();

        String sender = "<script>";
        String recipient = "<script>";
        String content = "<script>";

        MessageEntity message = factory.createSanitizedMessage(sender, recipient, content);

        assertEquals("&lt;script&gt;", message.getSender());
        assertEquals("&lt;script&gt;", message.getRecipient());
        assertEquals("&lt;script&gt;", message.getContent());
    }
}