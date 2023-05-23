package com.dslab.commonapi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.servlet.http.HttpServletResponse;


/**
 * @author gzy
 * @date 2022-11-09
 */
@Data
@Accessors(chain = true)
@ApiModel
public class Result<T> {
	@ApiModelProperty("状态码")
	private Integer statusCode;

	@ApiModelProperty("返回（出错）信息")
	private String msg;

	@ApiModelProperty("返回数据")
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
		result.msg ="执行异常:" + msg;
		return result;
	}

	public static <T> Result<T> error(Integer statusCode, String msg) {
		Result<T> result = new Result<>();
		result.statusCode = statusCode;
		result.msg = msg;
		return result;
	}

	public boolean isSuccess() {
		return this.statusCode==HttpServletResponse.SC_OK;
	}


	public Result<T> data(T data) {
		this.data = data;
		return this;
	}
}
