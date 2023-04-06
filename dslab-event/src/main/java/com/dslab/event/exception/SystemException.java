package com.dslab.event.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: dslab-event
 * @description: 捕获系统异常
 * @author: 郭晨旭
 * @create: 2023-03-30 13:40
 * @version: 1.0
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class SystemException extends RuntimeException {
    private Integer code;
    public SystemException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public SystemException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
