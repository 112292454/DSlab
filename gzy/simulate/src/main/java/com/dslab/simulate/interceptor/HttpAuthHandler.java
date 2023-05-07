package com.dslab.simulate.interceptor;

import com.dslab.simulate.util.WebsocketUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;

@Component

public class HttpAuthHandler extends TextWebSocketHandler {

    /**
     * socket 建立成功事件
     *
     * @param session
     * @throws Exception
     */

    @Override

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Object userId = session.getAttributes().get("uid");

        if (userId != null) {

            // 用户连接成功，放入在线用户缓存

            WebsocketUtil.addSession(userId.toString(), session);

        } else {

            throw new RuntimeException("用户登录已经失效!");

        }

    }

    /**
     * 接收消息事件
     *
     * @param session
     * @param message
     * @throws Exception
     */

    @Override

    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        // 获得客户端传来的消息

        String payload = message.getPayload();

        Object userId = session.getAttributes().get("uid");

        System.out.println("server 接收到 " + userId + " 发送的 " + payload);

        session.sendMessage(new TextMessage("server 发送给 " + userId + " 消息 " + payload + " " + LocalDateTime.now().toString()));

    }

    /**
     * socket 断开连接时
     *
     * @param session
     * @param status
     * @throws Exception
     */

    @Override

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        Object userId = session.getAttributes().get("uid");

        if (userId != null) {

            // 用户退出，移除缓存

            WebsocketUtil.removeSession(userId.toString());

        }

    }
}