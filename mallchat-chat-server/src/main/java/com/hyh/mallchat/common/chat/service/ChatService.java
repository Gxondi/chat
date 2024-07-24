package com.hyh.mallchat.common.chat.service;

import com.hyh.mallchat.common.chat.domain.dto.MsgReadInfoDTO;
import com.hyh.mallchat.common.chat.domain.entity.Message;
import com.hyh.mallchat.common.chat.domain.vo.req.*;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatMessageReadResp;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatMessageResp;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;

import java.util.Collection;

public interface ChatService {
    Long sendMsg(ChatMessageReq request ,Long uid);

    ChatMessageResp getMsgResp(Message message, Long receiveUid);

    CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, Long uid);

    void recallMsg(ChatMessageBaseReq request, Long uid);

    void setMsgMark(Long uid, ChatMessageMarkReq req);

    void readMsg(Long uid, ChatMessageMemberReq req);

    CursorPageBaseResp<ChatMessageReadResp> getReadOrUnReadPage(Long uid, ChatMessageReadReq req);

    Collection<MsgReadInfoDTO> readOrUnReadCount(Long uid, ChatMessageReadInfoReq req);

    void groupInfo(Long uid, long id);
}
