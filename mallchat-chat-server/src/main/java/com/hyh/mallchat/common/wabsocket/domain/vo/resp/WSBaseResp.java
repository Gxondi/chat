package com.hyh.mallchat.common.wabsocket.domain.vo.resp;

import com.hyh.mallchat.common.wabsocket.domain.enums.WSRespTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSBaseResp<T> {
    /**
     * 请求类型 1.请求登录二维码，2心跳检测
     *
     * @see WSRespTypeEnum
     */
    private Integer type;
    /**
     * 每个请求包具体的数据，类型不同结果不同
     */
    private T data;

}
