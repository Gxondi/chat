package com.hyh.mallchat.common.wabsocket.adapter;

import cn.hutool.json.JSONUtil;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.wabsocket.domain.enums.WSRespTypeEnum;
import com.hyh.mallchat.common.wabsocket.domain.vo.resp.WSBaseResp;
import com.hyh.mallchat.common.wabsocket.domain.vo.resp.WSLoginSuccess;
import com.hyh.mallchat.common.wabsocket.domain.vo.resp.WSLoginUrl;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class WebSocketAdapter {
    public static WSBaseResp buildResp(WxMpQrCodeTicket wxMpQrCodeTicket){
        //把码推送到前端
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return  resp;
    }

    public static WSBaseResp<?> buildResp(User user, String token) {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess wsLoginSuccess = new WSLoginSuccess();
        wsLoginSuccess.setUid(user.getId());
        wsLoginSuccess.setToken(token);
        wsLoginSuccess.setName(user.getName());
        wsLoginSuccess.setAvatar(user.getAvatar());
        resp.setData(wsLoginSuccess);
        return  resp;
    }

    public static WSBaseResp<?> buildWaitAuthorize() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }
    /**
     * 使前端的token失效，意味着前端需要重新登录
     * @return
     */
    public static WSBaseResp<?> buildInvalidTokenResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return resp;
    }
}
