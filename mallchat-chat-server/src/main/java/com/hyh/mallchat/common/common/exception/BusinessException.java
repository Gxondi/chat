package com.hyh.mallchat.common.common.exception;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
/**
 * 自定义异常处理
 */
@Data
public class BusinessException extends RuntimeException{
    private Integer errorCode;
    private String errorMsg;
    public BusinessException( String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }
    public BusinessException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
    public BusinessException(ErrorEnum errorEnum) {
        super(errorEnum.getErrorMsg());
        this.errorCode = errorEnum.getErrorCode();
        this.errorMsg = errorEnum.getErrorMsg();
    }
}
