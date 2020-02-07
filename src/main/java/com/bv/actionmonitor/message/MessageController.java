package com.bv.actionmonitor.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.security.Principal;

@Controller
@Validated
@Slf4j
class MessageController {

    @Autowired
    private MessageService messageService;

    @MessageMapping("/message.{recipient}")
    public void sendMessage(
            @Valid @NotNull @Size(min = 1, max = 255) @Payload String messageContent,
            @Valid @NotNull @Size(min = 1, max = 255) @DestinationVariable("recipient") String recipient,
            Principal principal) {

        System.out.println("YEYEYE");
        System.out.println(principal);
        String sender = principal.getName();
        messageService.sendMessage(sender, recipient, messageContent);
    }

    @MessageExceptionHandler
    public void handleException(Exception e) {
        log.warn("Error processing websocket message. Exception message: {}", e.getMessage());
    }
}
