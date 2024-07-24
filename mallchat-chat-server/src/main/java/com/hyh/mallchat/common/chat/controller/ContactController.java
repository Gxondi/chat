package com.hyh.mallchat.common.chat.controller;


import com.hyh.mallchat.common.chat.domain.vo.resp.ChatRoomResp;
import com.hyh.mallchat.common.chat.service.IContactService;
import com.hyh.mallchat.common.common.domain.vo.req.CursorPageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.req.FriendReqVo;
import com.hyh.mallchat.common.common.domain.vo.req.IdReqVO;
import com.hyh.mallchat.common.common.domain.vo.resp.ApiResult;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.hyh.mallchat.common.common.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 会话列表 前端控制器
 * </p>
 *
 * @author CondiX
 * @since 2024-05-21
 */
@RestController
@RequestMapping("/capi/chat")
@Api(tags = "聊天室相关接口")
public class ContactController {
    @Autowired
    private IContactService contactService;

    @GetMapping("/public/contact/page")
    @ApiOperation("会话列表")
    public ApiResult<CursorPageBaseResp<ChatRoomResp>> getRoomPage(@Valid CursorPageBaseReq req){
        return ApiResult.success(contactService.getContactPage(RequestHolder.getRequestInfo().getUid(),req));
    }
    @GetMapping("/public/contact/detail")
    @ApiOperation("会话详情")
    public ApiResult<ChatRoomResp> detail(@Valid IdReqVO reqVO){
        return ApiResult.success(contactService.getDetail(RequestHolder.getRequestInfo().getUid(),reqVO));
    }

    @GetMapping("/public/contact/detail/friend")
    @ApiOperation("会话详情(联系人列表发消息用)")
    public ApiResult<ChatRoomResp> getContactDetailByFriend(@Valid FriendReqVo reqVO){
        return ApiResult.success(contactService.getContactDetailByFriend(RequestHolder.getRequestInfo().getUid(),reqVO));
    }
}

