package com.hyh.mallchat.common.user.service.impl;

import com.hyh.mallchat.common.common.constant.MQConstant;
import com.hyh.mallchat.common.wabsocket.domain.dto.PushMsgDTO;
import com.hyh.mallchat.common.wabsocket.domain.vo.resp.WSBaseResp;
import com.hyh.mallchat.transaction.service.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PushService {
    @Autowired
    private MQProducer mqProducer;

    /**
     * 推送消息
     * @param wsBaseResp
     */
    public void sendAllOnlineUser(WSBaseResp<?> wsBaseResp) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC,new PushMsgDTO(wsBaseResp));
    }

    public void sendGroupMsg(WSBaseResp<?> wsBaseResp, List<Long> memeberList) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC,new PushMsgDTO(wsBaseResp,memeberList));
    }

    public void sendPushMsg(Long targetId, WSBaseResp<?> wsBaseResp) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC,new PushMsgDTO(wsBaseResp,targetId));
    }

    public void sendPushMsg(WSBaseResp<?> wsBaseResp) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC,new PushMsgDTO(wsBaseResp));
    }
}
