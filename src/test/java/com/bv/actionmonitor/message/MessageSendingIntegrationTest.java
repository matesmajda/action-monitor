package com.bv.actionmonitor.message;

import com.bv.actionmonitor.util.SimpleStompSessionHandler;
import com.bv.actionmonitor.util.UserMessageHandler;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static com.bv.actionmonitor.config.WebSocketConfig.MESSAGES_ENDPOINT;
import static com.bv.actionmonitor.util.WebsocketUtils.getAuthHeaders;
import static com.bv.actionmonitor.util.WebsocketUtils.newStompClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class MessageSendingIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    public void connectsToSocket() {
        WebSocketStompClient client = newStompClient();
        StompSession stompSession = initStompSession(client, "user1", "pass");
        assertThat(stompSession.isConnected()).isTrue();
    }

    @Test
    @Order(2)
    public void exchangeMessagesBetweenTwoUsers() throws Exception {
        WebSocketStompClient client1 = newStompClient();
        WebSocketStompClient client2 = newStompClient();

        StompSession session1 = initStompSession(client1, "user1", "pass");
        StompSession session2 = initStompSession(client2, "user2", "pass");

        UserMessageHandler user1MessageHandler = new UserMessageHandler();
        UserMessageHandler user2MessageHandler = new UserMessageHandler();
        session1.subscribe("/user/queue/message", user1MessageHandler);
        session2.subscribe("/user/queue/message", user2MessageHandler);

        //user1 sends to user2
        session1.send("/app/message.user2", "Hello there");
        UserMessage message1 = user2MessageHandler.getMessage(2);
        assertThat(message1.getContent()).isEqualTo("Hello there");
        assertThat(message1.getSender()).isEqualTo("user1");

        //user2 sends to user1
        session2.send("/app/message.user1", "General Kenobi");
        UserMessage message2 = user1MessageHandler.getMessage(2);
        assertThat(message2.getContent()).isEqualTo("General Kenobi");
        assertThat(message2.getSender()).isEqualTo("user2");

        client1.stop();
        client2.stop();
    }

    private StompSession initStompSession(WebSocketStompClient stompClient, String username, String password) {
        WebSocketHttpHeaders httpHeaders = getAuthHeaders(username, password);
        try {
            return stompClient.connect(getWsUrl(), httpHeaders, new StompHeaders(), new SimpleStompSessionHandler()).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getWsUrl() {
        return "ws://localhost:" + port + MESSAGES_ENDPOINT + "/websocket";
    }
}