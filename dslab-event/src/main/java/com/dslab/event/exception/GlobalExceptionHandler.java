package com.dslab.event.exception;

import com.dslab.event.vo.Result;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * @program: dslab-event
 * @description: 捕获全局异常
 * @author: 郭晨旭
 * @create: 2023-04-05 18:22
 * @version: 1.0
 **/
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * http 异常处理
     */
    @ExceptionHandler(HttpException.class)
    public Result httpExceptionHandler(HttpException e) {
        return Result.error(e.getMessage());
    }

    /**
     * 验证失败异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return Result.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "错误的请求参数").data(e.getMessage());
    }

    /**
     * 运行时逻辑异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        logger.error("异常测试信息1", e);
        return Result.error("运行时错误").data(e.getMessage());
    }

    /**
     * 默认异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e) {
        logger.error("异常测试信息2", e);
        return Result.error("服务器未知错误").data(e.getMessage());
    }
}