package com.example.login.config;

import com.dslab.commonapi.vo.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author gzy
 * @date 2022-11-13
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

	private static Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);
	/**
	 * http 异常处理
	 * @param e
	 * @return
	 */
	@ExceptionHandler(HttpException.class)
	public Result httpExceptionHandler(HttpException e) {
		return Result.error(e.getMessage());
	}

	/**
	 * 验证失败异常处理
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		return Result.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "错误的请求参数").data(e.getMessage());
	}

	/**
	 * 运行时逻辑异常
	 * @param e
	 * @return
	 */
	@ExceptionHandler(RuntimeException.class)
	public Result handleRuntimeException(RuntimeException e) {
		logger.error("异常测试信息1", e);
		return Result.error("运行时错误").data(e.getMessage());
	}
	/**
	 * 默认异常处理
	 * @param e
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public Result exceptionHandler(Exception e) {
		logger.error("异常测试信息2", e);
		e.printStackTrace();
		return Result.error( "服务器未知错误").data(e.getMessage());
	}
}