package com.hyh.mallchat.common.wabsocket.adapter;

import com.hyh.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.wabsocket.domain.enums.WSRespTypeEnum;
import com.hyh.mallchat.common.wabsocket.domain.vo.resp.*;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class WebSocketAdapter {
    public static WSBaseResp buildResp(WxMpQrCodeTicket wxMpQrCodeTicket){
        //把码推送到前端
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return  resp;
    }

    public static WSBaseResp<?> buildResp(User user, String token,boolean hasPower) {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess wsLoginSuccess = new WSLoginSuccess();
        wsLoginSuccess.setUid(user.getId());
        wsLoginSuccess.setToken(token);
        wsLoginSuccess.setName(user.getName());
        wsLoginSuccess.setAvatar(user.getAvatar());
        wsLoginSuccess.setPower(hasPower? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
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

    /**
     * 用户被拉黑
     * @param id
     * @return
     */
    public static WSBaseResp<?> buildBlack(Long id) {
        WSBaseResp<WSBlack> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.BLACK.getType());
        WSBlack wsBlack = new WSBlack();
        wsBlack.setUid(id);
        resp.setData(wsBlack);
        return  resp;
    }


    public static WSBaseResp<?> buildUserApplyMsg(Long uid, Integer unReadCount) {
        WSBaseResp<WSFriendApply> resp = new WSBaseResp();
        WSFriendApply wsFriendApply = new WSFriendApply();
        wsFriendApply.setUid(uid);
        wsFriendApply.setUnreadCount(unReadCount);
        resp.setData(wsFriendApply);
        resp.setType(WSRespTypeEnum.APPLY.getType());
        return resp;
    }
}
