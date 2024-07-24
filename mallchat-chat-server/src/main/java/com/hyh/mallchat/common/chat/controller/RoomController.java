package com.hyh.mallchat.common.chat.controller;


import com.hyh.mallchat.common.chat.service.ChatService;
import com.hyh.mallchat.common.common.domain.vo.req.IdReqVO;
import com.hyh.mallchat.common.common.domain.vo.resp.ApiResult;
import com.hyh.mallchat.common.common.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 房间表 前端控制器
 * </p>
 *
 * @author CondiX
 * @since 2024-05-21
 */
@RestController
@RequestMapping("/capi/room")
@Api(tags = "聊天室相关接口")
public class RoomController {
    @Autowired
    private ChatService chatService;
    @GetMapping("public/group")
    @ApiOperation("群组详情")
    public ApiResult<Void> groupInfo(@RequestBody @Valid IdReqVO request){
        chatService.groupInfo(RequestHolder.getRequestInfo().getUid(),request.getId());
        return ApiResult.success();
    }

}

