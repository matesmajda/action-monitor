package com.bv.actionmonitor.config;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String MESSAGES_ENDPOINT = "/messages";

    @Value("${queue.protocol}")
    private String protocol;

    @Value("${queue.host}")
    private String host;

    @Value("${queue.port}")
    private Integer port;

    @Value("${queue.username}")
    private String username;

    @Value("${queue.password}")
    private String password;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(MESSAGES_ENDPOINT).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableStompBrokerRelay("/queue/")
                .setRelayHost(host)
                .setRelayPort(port)
                .setClientLogin(username)
                .setClientPasscode(password);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public BrokerService broker() throws Exception {
        final BrokerService broker = new BrokerService();
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(protocol).host(host).port(port).build();
        broker.addConnector(uriComponents.toUri());
        broker.setPersistent(false);
        final ManagementContext managementContext = new ManagementContext();
        managementContext.setCreateConnector(true);
        broker.setManagementContext(managementContext);
        return broker;
    }
}