package com.hyh.mallchat.common.wabsocket.domain.vo.req;

import com.hyh.mallchat.common.wabsocket.domain.enums.WSReqTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: websocket前端请求体
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSBaseReq<T> {
    /**
     * 请求类型 1.请求登录二维码，2心跳检测
     *
     * @see WSReqTypeEnum
     */
    private Integer type;
    /**
     * 每个请求包具体的数据，类型不同结果不同
     */
    private T data;
}
