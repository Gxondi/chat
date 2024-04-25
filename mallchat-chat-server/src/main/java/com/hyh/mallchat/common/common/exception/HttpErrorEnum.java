package com.hyh.mallchat.common.common.exception;

import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Charsets;
import com.hyh.mallchat.common.common.domain.vo.resp.ApiResult;
import com.hyh.mallchat.common.common.utils.JsonUtils;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
public enum HttpErrorEnum {
    ACCESS_DENIED(401, "登陆失效，请重新登陆");
    private Integer code;
    private String message;
//    response.setStatus(401);
//                response.setContentType("application/json;charset=utf-8");
//                response.wait();
    public void sendHttpError(HttpServletResponse response) throws IOException {
        response.setStatus(code);
        response.setContentType(ContentType.JSON.toString(Charsets.UTF_8));
        response.getWriter().write(JsonUtils.toStr(ApiResult.fail(code, message).toString()));
    }

}
