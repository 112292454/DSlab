package com.dslab.simulate.config;

import com.dslab.simulate.interceptor.HttpAuthHandler;
import com.dslab.simulate.interceptor.WebsocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration

@EnableWebSocket

public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired

    private HttpAuthHandler httpAuthHandler;

    @Autowired

    private WebsocketInterceptor myInterceptor;

    @Override

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry

                .addHandler(httpAuthHandler, "myWS")

                .addInterceptors(myInterceptor)

                .setAllowedOrigins("*");

    }

}