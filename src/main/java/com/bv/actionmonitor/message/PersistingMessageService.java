package com.bv.actionmonitor.message;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class PersistingMessageService implements MessageService {

    static final String MESSAGE_QUEUE_DESTINATION = "/queue/message";

    private SimpMessagingTemplate simpMessagingTemplate;
    private MessageRepository messageRepository;
    private MessageFactory messageFactory;

    @Override
    public void sendMessage(String sender, String recipient, String content) {
        MessageEntity messageEntity = saveMessage(sender, recipient, content);
        sendMessageToDestination(messageEntity);
    }

    private MessageEntity saveMessage(String sender, String recipient, String content) {
        MessageEntity message = messageFactory.createSanitizedMessage(sender, recipient, content);
        return messageRepository.save(message);
    }

    private void sendMessageToDestination(MessageEntity message) {
        UserMessage messageToSend = UserMessage.builder()
                .sender(message.getSender())
                .content(message.getContent())
                .date(message.getCreatedAt())
                .build();
        simpMessagingTemplate.convertAndSendToUser(message.getRecipient(), MESSAGE_QUEUE_DESTINATION, messageToSend);
    }
}
