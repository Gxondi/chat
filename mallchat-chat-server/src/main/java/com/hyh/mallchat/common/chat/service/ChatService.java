package com.hyh.mallchat.common.chat.service;

import com.hyh.mallchat.common.chat.domain.entity.Message;
import com.hyh.mallchat.common.chat.domain.vo.req.ChatMessageBaseReq;
import com.hyh.mallchat.common.chat.domain.vo.req.ChatMessageMarkReq;
import com.hyh.mallchat.common.chat.domain.vo.req.ChatMessagePageReq;
import com.hyh.mallchat.common.chat.domain.vo.req.ChatMessageReq;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatMessageResp;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;

public interface ChatService {
    Long sendMsg(ChatMessageReq request ,Long uid);

    ChatMessageResp getMsgResp(Message message, Long receiveUid);

    CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, Long uid);

    void recallMsg(ChatMessageBaseReq request, Long uid);

    void setMsgMark(Long uid, ChatMessageMarkReq req);
}
