package com.hyh.mallchat.common.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;


public interface ErrorEnum {

        Integer getErrorCode();

        String getErrorMsg();
}
