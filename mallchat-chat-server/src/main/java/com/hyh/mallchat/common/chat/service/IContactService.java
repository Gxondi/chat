package com.hyh.mallchat.common.chat.service;

import com.hyh.mallchat.common.chat.domain.dto.MsgReadInfoDTO;
import com.hyh.mallchat.common.chat.domain.entity.Message;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatRoomResp;
import com.hyh.mallchat.common.common.domain.vo.req.CursorPageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.req.FriendReqVo;
import com.hyh.mallchat.common.common.domain.vo.req.IdReqVO;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会话列表 服务类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-15
 */
public interface IContactService{

    CursorPageBaseResp<ChatRoomResp> getContactPage(Long uid, CursorPageBaseReq req);

    ChatRoomResp getDetail(Long uid, @Valid IdReqVO reqVO);

    ChatRoomResp getContactDetailByFriend(Long uid, FriendReqVo reqVO);

    Map<Long, MsgReadInfoDTO> getMsgReadInfo(List<Message> messages);
}
