package com.bv.actionmonitor.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Date;

import static com.bv.actionmonitor.message.PersistingMessageService.MESSAGE_QUEUE_DESTINATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersistingMessageServiceTest {

    @Mock
    private MessageFactory messageFactory;
    @Mock
    private MessageRepository repository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        messageService = new PersistingMessageService(messagingTemplate, repository, messageFactory);
    }

    @Test
    void sendMessagePersistsMessage() {

        String sender = "sender";
        String recipient = "recipient";
        String content = "content";

        MessageEntity messageEntity = new MessageEntity(1L, sender, recipient, content, new Date());
        when(messageFactory.createSanitizedMessage(sender, recipient, content)).thenReturn(messageEntity);
        when(repository.save(messageEntity)).thenReturn(messageEntity);

        messageService.sendMessage(sender, recipient, content);

        verify(repository).save(messageEntity);
    }

    @Test
    void sendMessageSendsMessageToUser() {

        String sender = "sender";
        String recipient = "recipient";
        String content = "content";
        Date date = new Date();

        MessageEntity message = new MessageEntity(1L, sender, recipient, content, date);
        when(messageFactory.createSanitizedMessage(sender, recipient, content)).thenReturn(message);
        when(repository.save(message)).thenReturn(message);

        messageService.sendMessage(sender, recipient, content);

        UserMessage messageToSend = UserMessage.builder().sender(sender).content(content).date(date).build();
        verify(messagingTemplate).convertAndSendToUser(message.getRecipient(), MESSAGE_QUEUE_DESTINATION, messageToSend);
    }
}