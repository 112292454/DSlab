package com.dslab.event.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.servlet.http.HttpServletResponse;

/**
 * @program: dslab-event
 * @description: 请求返回数据
 * @author: 郭晨旭
 * @create: 2023-03-29 00:19
 * @version: 1.0
 **/
@Data
@Accessors(chain = true)
public class Result<T> {
    /**
     * 状态码
     */
    private Integer statusCode;
    /**
     * 返回信息
     */
    private String msg;
    /**
     * 返回数据
     */
    private T data;

    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<>();
        result.statusCode = HttpServletResponse.SC_OK;
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.statusCode = HttpServletResponse.SC_OK;
        result.msg = "请求成功";
        return result;
    }

    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        result.msg = "执行异常";
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        result.msg = "执行异常:" + msg;
        return result;
    }

    public static <T> Result<T> error(Integer statusCode, String msg) {
        Result<T> result = new Result<>();
        result.statusCode = statusCode;
        result.msg = msg;
        return result;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }
}
