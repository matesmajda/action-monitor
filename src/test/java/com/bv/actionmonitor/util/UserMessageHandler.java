package com.bv.actionmonitor.util;

import com.bv.actionmonitor.message.UserMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UserMessageHandler implements StompFrameHandler {

    private final CompletableFuture<UserMessage> frameHandler = new CompletableFuture<>();

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return UserMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        log.info("received message: {} with headers: {}", payload, headers);
        frameHandler.complete((UserMessage) payload);
    }

    public UserMessage getMessage(int timeoutSeconds) throws Exception {
        return frameHandler.get(timeoutSeconds, TimeUnit.SECONDS);
    }
}