package com.bv.actionmonitor.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Slf4j
public class WebsocketUtils {

    private WebsocketUtils() {}

    public static WebSocketStompClient newStompClient() {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient;
    }

    public static WebSocketHttpHeaders getAuthHeaders(String username, String password) {
        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
        httpHeaders.add("Authorization", getBasicAuthHeaderValue(username, password));
        return httpHeaders;
    }

    private static String getBasicAuthHeaderValue(String username, String password) {
        byte[] plainCredentials = (username + ":" + password).getBytes();
        String encodedCredentials = new String(Base64.encodeBase64(plainCredentials));
        return "Basic " + encodedCredentials;
    }
}