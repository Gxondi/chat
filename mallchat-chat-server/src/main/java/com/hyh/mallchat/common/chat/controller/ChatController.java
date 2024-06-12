package com.hyh.mallchat.common.chat.controller;


import com.hyh.mallchat.common.chat.dao.MessageDao;
import com.hyh.mallchat.common.chat.domain.entity.msg.MsgRecall;
import com.hyh.mallchat.common.chat.domain.vo.req.ChatMessageBaseReq;
import com.hyh.mallchat.common.chat.domain.vo.req.ChatMessageMarkReq;
import com.hyh.mallchat.common.chat.domain.vo.req.ChatMessagePageReq;
import com.hyh.mallchat.common.chat.domain.vo.req.ChatMessageReq;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatMessageResp;
import com.hyh.mallchat.common.chat.service.ChatService;
import com.hyh.mallchat.common.common.domain.vo.req.CursorPageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.resp.ApiResult;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.hyh.mallchat.common.common.utils.RequestHolder;
import com.hyh.mallchat.common.user.domain.dto.MsgRecallDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerInterceptor;

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
@Slf4j
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private MessageDao messageDao;


    @GetMapping("/public/msg/page")
    @ApiOperation("获取消息列表")
    public ApiResult<CursorPageBaseResp<ChatMessageResp>> getMsgPage(@RequestBody @Valid ChatMessagePageReq request) {
        CursorPageBaseResp<ChatMessageResp> page = chatService.getMsgPage(request, RequestHolder.getRequestInfo().getUid());
        return ApiResult.success(page);
    }

    @PostMapping("/msg/send")
    @ApiOperation("发送消息")
    public ApiResult<ChatMessageResp> sendMsg(@RequestBody @Valid ChatMessageReq request) {
        Long msgId = chatService.sendMsg(request, RequestHolder.getRequestInfo().getUid());
        return ApiResult.success(chatService.getMsgResp(messageDao.getById(msgId),RequestHolder.getRequestInfo().getUid()));
    }
    @PutMapping("/msg/recall")
    @ApiOperation("撤回消息")
    public ApiResult<Void> recallMsg(@RequestBody @Valid ChatMessageBaseReq request) {
        chatService.recallMsg(request, RequestHolder.getRequestInfo().getUid());
        return ApiResult.success();
    }

    @PutMapping("/msg/mark")
    @ApiOperation("标记消息")
    public ApiResult<Void> setMsgMark(@RequestBody @Valid ChatMessageMarkReq req){
        chatService.setMsgMark(RequestHolder.getRequestInfo().getUid(),req);
        return ApiResult.success();
    }

}

