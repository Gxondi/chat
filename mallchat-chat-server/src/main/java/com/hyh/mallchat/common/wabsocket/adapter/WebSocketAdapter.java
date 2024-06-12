package com.hyh.mallchat.common.wabsocket.adapter;

import com.hyh.mallchat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.hyh.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatMessageResp;
import com.hyh.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.hyh.mallchat.common.user.domain.dto.MsgRecallDTO;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.wabsocket.domain.enums.WSRespTypeEnum;
import com.hyh.mallchat.common.wabsocket.domain.vo.resp.*;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.BeanUtils;

import java.util.Collections;

public class WebSocketAdapter {
    public static WSBaseResp buildResp(WxMpQrCodeTicket wxMpQrCodeTicket){
        //把码推送到前端
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return  resp;
    }

    public static WSBaseResp<WSLoginSuccess> buildResp(User user, String token,boolean hasPower) {
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

    public static WSBaseResp<WSLoginUrl> buildWaitAuthorize() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }
    /**
     * 使前端的token失效，意味着前端需要重新登录
     * @return
     */
    public static WSBaseResp<WSLoginUrl> buildInvalidTokenResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return resp;
    }

    /**
     * 用户被拉黑
     * @param id
     * @return
     */
    public static WSBaseResp<WSBlack> buildBlack(Long id) {
        WSBaseResp<WSBlack> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.BLACK.getType());
        WSBlack wsBlack = new WSBlack();
        wsBlack.setUid(id);
        resp.setData(wsBlack);
        return  resp;
    }


    public static WSBaseResp<WSFriendApply> buildUserApplyMsg(Long uid, Integer unReadCount) {
        WSBaseResp<WSFriendApply> resp = new WSBaseResp();
        WSFriendApply wsFriendApply = new WSFriendApply();
        wsFriendApply.setUid(uid);
        wsFriendApply.setUnreadCount(unReadCount);
        resp.setData(wsFriendApply);
        resp.setType(WSRespTypeEnum.APPLY.getType());
        return resp;
    }

    public static WSBaseResp<ChatMessageResp> buildMsgSend(ChatMessageResp chatMessageResp) {
        WSBaseResp<ChatMessageResp> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.MESSAGE.getType());
        resp.setData(chatMessageResp);
        return resp;
    }

    public static WSBaseResp<WSOnlineOfflineNotify> buildOfflineNotifyResp(User user) {
        WSBaseResp<WSOnlineOfflineNotify> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.ONLINE_OFFLINE_NOTIFY.getType());
        WSOnlineOfflineNotify onlineOfflineNotify = new WSOnlineOfflineNotify();
        //TODO 离线
        onlineOfflineNotify.setChangeList(null);
        //TODO 在线人数
        onlineOfflineNotify.setOnlineNum(20L);
        resp.setData(onlineOfflineNotify);
        return resp;
    }


    public static WSBaseResp<WSMsgRecall> buildRecallMsg(MsgRecallDTO recallDTO) {
        WSBaseResp<WSMsgRecall> resp = new WSBaseResp();
        resp.setType(MessageTypeEnum.RECALL.getType());
        WSMsgRecall wsMsgRecall = new WSMsgRecall();
        wsMsgRecall.setMsgId(recallDTO.getMsgId());
        wsMsgRecall.setRoomId(recallDTO.getRoomId());
        wsMsgRecall.setRecallUid(recallDTO.getRecallUid());
        resp.setData(wsMsgRecall);
        return resp;
    }


    public static WSBaseResp<WSMsgMark> buildMsgMark(ChatMessageMarkDTO dto, Integer markCount) {
        WSMsgMark.WSMsgMarkItem wsMsgMarkItem = new WSMsgMark.WSMsgMarkItem();
        BeanUtils.copyProperties(dto,wsMsgMarkItem);
        wsMsgMarkItem.setMarkCount(markCount);
        WSBaseResp<WSMsgMark> resp = new WSBaseResp();
        resp.setType(WSRespTypeEnum.MARK.getType());
        WSMsgMark wsMsgMark = new WSMsgMark();
        wsMsgMark.setMarkList(Collections.singletonList(wsMsgMarkItem));
        resp.setData(wsMsgMark);
        return resp;
    }
}
