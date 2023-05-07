package com.dslab.simulate.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebsocketInterceptor implements HandshakeInterceptor {
    /**
     * 握手前
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        System.out.println("握手开始");

        // 获得请求参数

        Map<String, String> paramMap =getHeaders(request);

        String uid = paramMap.get("uid");

        System.out.println("获取到用户uid为：" + uid);

        if (StringUtils.isNotBlank(uid)) {

            // 放入属性域

            attributes.put("uid", uid);

            System.out.println("用户 uid " + uid + " 握手成功！");

            return true;

        }

        System.out.println("用户登录已失效");

        return false;

    }
    private static Map<String, String> getHeaders(ServerHttpRequest request) {
//        Map<String, String> headerMap = new HashMap<>();
        HttpHeaders headers = request.getHeaders();
        Map<String, String> singleValueMap = headers.toSingleValueMap();
//        while (enumeration.hasMoreElements()) {
//            String name	= enumeration.nextElement();
//            String value = request.getHeader(name);
//            headerMap.put(name, value);
//        }
        return singleValueMap;
    }

    /**
     * 握手后
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param exception
     */

    @Override

    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

        System.out.println("握手完成");

    }
}
