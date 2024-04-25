package com.hyh.mallchat.common.common.exception;

import com.hyh.mallchat.common.common.domain.vo.resp.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 名字参数校验异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResult<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage());
        StringBuilder errorMsg = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        fieldErrors.forEach(x->{errorMsg.append(x.getField()).append(x.getDefaultMessage()).append(",");});
        String message = errorMsg.toString();
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getErrorCode(),message.substring(0,message.length()-1));
    }
    @ExceptionHandler(value = BusinessException.class)
    public ApiResult<?> businessExceptionHandler(BusinessException e) {
        log.info("BusinessException: {}", e.getMessage(),e);
        return ApiResult.fail(e.getErrorCode(), e.getErrorMsg());
    }
    @ExceptionHandler(value = Throwable.class)
    public ApiResult<?> exceptionHandler(Throwable throwable) {
        log.error("Exception: {}", throwable.getMessage(),throwable);
        return ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR);
    }
}
