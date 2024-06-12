package com.hyh.mallchat.common.user.controller;
import com.hyh.mallchat.common.common.domain.vo.req.CursorPageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.req.PageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.resp.ApiResult;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.hyh.mallchat.common.common.domain.vo.resp.PageBaseResp;
import com.hyh.mallchat.common.common.utils.RequestHolder;
import com.hyh.mallchat.common.user.domain.vo.req.FriendApplyReq;
import com.hyh.mallchat.common.user.domain.vo.req.FriendApproveReq;
import com.hyh.mallchat.common.user.domain.vo.req.FriendCheckReq;
import com.hyh.mallchat.common.user.domain.vo.req.FriendDeleteReq;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendApplyResp;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendCheckResp;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendResp;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendUnReadCountResp;
import com.hyh.mallchat.common.user.service.IUserFriendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import javax.validation.Valid;

/**
 * <p>
 * 用户联系人表 前端控制器
 * </p>
 *
 * @author CondiX
 * @since 2024-05-11
 */
@Controller
@RequestMapping("capi/user/friend")
@Api(tags = "好友列表接口")
public class UserFriendController {
    @Autowired
    private IUserFriendService userFriendService;

    @GetMapping("page")
    @ApiOperation("好友列表")
    public ApiResult<CursorPageBaseResp<FriendResp>> list(@RequestBody @Valid CursorPageBaseReq request) {
        Long uid = RequestHolder.getRequestInfo().getUid();
        CursorPageBaseResp<FriendResp> userFriendList = userFriendService.friendList(uid, request);
        return ApiResult.success(userFriendList);
    }
    @GetMapping("check")
    @ApiOperation("检查是否是好友")
    public ApiResult<FriendCheckResp> check(@Valid FriendCheckReq request){
        Long uid = RequestHolder.getRequestInfo().getUid();
        return ApiResult.success(userFriendService.checkFriend(uid, request));
    }
    @GetMapping("apply/page")
    @ApiOperation("好友申请列表")
    public ApiResult<PageBaseResp<FriendApplyResp>> applyList(@Valid PageBaseReq request){
        Long uid = RequestHolder.getRequestInfo().getUid();
        return ApiResult.success(userFriendService.getPageFriendApply(uid, request));
    }
    @PostMapping("apply")
    @ApiOperation("申请添加好友")
    public ApiResult<Void> apply(@RequestBody @Valid FriendApplyReq request){
        Long uid = RequestHolder.getRequestInfo().getUid();
        userFriendService.apply(uid, request);
        return ApiResult.success();
    }
    @PostMapping("approve")
    @ApiOperation("同意好友申请")
    public ApiResult<Void> approve(@RequestBody @Valid FriendApproveReq request){
        Long uid = RequestHolder.getRequestInfo().getUid();
        userFriendService.applyApprove(uid, request);
        return ApiResult.success();
    }
    @GetMapping("apply/unread")
    @ApiOperation("未读好友申请")
    public ApiResult<FriendUnReadCountResp> unRead(){
        Long uid = RequestHolder.getRequestInfo().getUid();
        return ApiResult.success(userFriendService.unRead(uid));
    }
    @DeleteMapping("delete")
    @ApiOperation("删除好友")
    public ApiResult<Void> delete(@RequestBody @Valid FriendDeleteReq request){
        Long uid = RequestHolder.getRequestInfo().getUid();
        userFriendService.delete(uid, request);
        return ApiResult.success();
    }
}

