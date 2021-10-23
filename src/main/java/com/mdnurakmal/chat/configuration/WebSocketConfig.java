package com.mdnurakmal.chat.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // chat client will use this to connect to the server
        registry.addEndpoint("/").setAllowedOrigins("http://localhost:4200","https://mdnurakmal.com","https://wschat.mdnurakmal.com"
        ,"wss://wschat.mdnurakmal.com","ws://wschat.mdnurakmal.com");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        ThreadPoolTaskScheduler te = new ThreadPoolTaskScheduler();
        te.setPoolSize(1);
        te.setThreadNamePrefix("wss-heartbeat-thread-");
        te.initialize();

        registry.enableSimpleBroker("/")
                /**
                 * Configure the value for the heartbeat settings. The first number
                 * represents how often the server will write or send a heartbeat.
                 * The second is how often the client should write. 0 means no heartbeats.
                 * <p>By default this is set to "0, 0" unless the {@link #setTaskScheduler
                 * taskScheduler} in which case the default becomes "10000,10000"
                 * (in milliseconds).
                 * @since 4.2
                 */
                .setHeartbeatValue(new long[]{0, 1000})
                .setTaskScheduler(te);

        registry.setApplicationDestinationPrefixes("/app");
    }
}