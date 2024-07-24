package com.hyh.mallchat.common.chat.controller;


import com.hyh.mallchat.common.chat.dao.MessageDao;
import com.hyh.mallchat.common.chat.domain.dto.MsgReadInfoDTO;
import com.hyh.mallchat.common.chat.domain.vo.req.*;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatMessageReadResp;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatMessageResp;
import com.hyh.mallchat.common.chat.service.ChatService;
import com.hyh.mallchat.common.common.domain.vo.resp.ApiResult;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.hyh.mallchat.common.common.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

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
    @PutMapping("/msg/read")
    @ApiOperation("阅读上报")
    public ApiResult<Void> readMsg(@RequestBody @Valid ChatMessageMemberReq req){
        chatService.readMsg(RequestHolder.getRequestInfo().getUid(),req);
        return ApiResult.success();
    }
    @GetMapping("/msg/read/page")
    @ApiOperation("消息已读未读列表")
    public ApiResult<CursorPageBaseResp<ChatMessageReadResp>> readOrUnReadPage(@RequestBody @Valid ChatMessageReadReq req){
        return ApiResult.success( chatService.getReadOrUnReadPage(RequestHolder.getRequestInfo().getUid(),req));
    }
    @GetMapping("/msg/read")
    @ApiOperation("消息已读未读数")
    public ApiResult<Collection<MsgReadInfoDTO>> readOrUnReadCount(@RequestBody @Valid ChatMessageReadInfoReq req){
        return ApiResult.success(chatService.readOrUnReadCount(RequestHolder.getRequestInfo().getUid(),req));
    }
}

