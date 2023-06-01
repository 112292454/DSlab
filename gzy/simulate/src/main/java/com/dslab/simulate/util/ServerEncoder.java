package com.dslab.simulate.util;

import com.alibaba.fastjson2.JSON;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;

/**
 * definition for our encoder
 *
 * @编写人: 夏小雪 日期:2015年6月14日 时间:上午11:58:23
 */
public class ServerEncoder implements Encoder.Text<Object> {

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(EndpointConfig arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public String encode(Object obj) throws EncodeException {

        return JSON.toJSONString(obj);

    }

}