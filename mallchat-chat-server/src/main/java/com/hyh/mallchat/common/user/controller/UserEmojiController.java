package com.hyh.mallchat.common.user.controller;


import com.hyh.mallchat.common.common.domain.vo.resp.ApiResult;
import com.hyh.mallchat.common.common.domain.vo.resp.IdRespVO;
import com.hyh.mallchat.common.common.utils.RequestHolder;
import com.hyh.mallchat.common.user.domain.entity.UserEmoji;
import com.hyh.mallchat.common.user.domain.vo.req.UserEmojiReq;
import com.hyh.mallchat.common.user.domain.vo.resp.UserEmojiResp;
import com.hyh.mallchat.common.user.service.IUserEmojiService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表情包 前端控制器
 * </p>
 *
 * @author CondiX
 * @since 2024-06-06
 */
@RestController
@RequestMapping("/capi/userEmoji")
public class UserEmojiController {
    @Autowired
    private IUserEmojiService emojiService;
    /**
     * 表情包列表
     *
     * @return 表情包列表
     */
    @GetMapping("/list")
    @ApiOperation("表情包列表")
    public ApiResult<List<UserEmojiResp>> getEmojiList() {
        return ApiResult.success(emojiService.getEmojiList(RequestHolder.getRequestInfo().getUid()));
    }
    @PostMapping("/add")
    @ApiOperation("新增表情包")
    public ApiResult<IdRespVO> addEmoji(@Valid @RequestBody UserEmojiReq req) {
        IdRespVO idRespVO = emojiService.addEmoji(RequestHolder.getRequestInfo().getUid(), req);
        return ApiResult.success(idRespVO);
    }
    @DeleteMapping("/delete")
    @ApiOperation("删除表情包")
    public ApiResult<Void> deleteEmoji(@Valid @RequestBody IdRespVO idRespVO) {
        emojiService.deleteEmoji(RequestHolder.getRequestInfo().getUid(), idRespVO.getId());
        return ApiResult.success();
    }

}

