package com.dslab.simulate.util;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * WebSocket 服务类
 **/
@ServerEndpoint("/simulate/ws/{uid}")
@Component
public class WebSocketServer {
    static Log log = LogFactory.getLog(WebSocketServer.class);
    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;


    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收 uid
    private String uid = "";

    public String getUid() {
        return uid;
    }


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid){
        this.session = session;
        this.uid = uid;
        WebSocketUtil.add(this);
        addOnlineCount();   //在线数加1
        try {
            sendMessage("WebSocket连接成功！");
            log.info("有新窗口开始监听:" + uid + ",当前在线人数为:" + getOnlineCount());
        } catch (IOException e) {
            log.error("websocket IO Exception");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        WebSocketUtil.remove(this); // 从 set 中删除
        subOnlineCount(); // 在线数减 1
        log.info(" 有一连接关闭,窗口为：" + uid + "！当前在线人数为 " + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {

        log.info(" 收到来自窗口 " + uid + " 的信息 :" + message);

        String returnMessage = "你刚才说：" + message;
        try {
            session.getBasicRemote().sendText(returnMessage);
        } catch (IOException e) {
            System.out.println("返回数据失败");
        }

    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error(" 发生错误 ");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }




}