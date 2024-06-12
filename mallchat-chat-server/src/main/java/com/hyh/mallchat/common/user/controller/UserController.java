package com.hyh.mallchat.common.user.controller;


import com.hyh.mallchat.common.common.annotation.RedissonLock;
import com.hyh.mallchat.common.common.domain.dto.RequestInfo;
import com.hyh.mallchat.common.common.domain.enums.RoleEnum;
import com.hyh.mallchat.common.common.domain.vo.resp.ApiResult;
import com.hyh.mallchat.common.common.interceptor.TokenInterceptor;
import com.hyh.mallchat.common.common.utils.AssertUtil;
import com.hyh.mallchat.common.common.utils.RequestHolder;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.dto.ItemInfoDTO;
import com.hyh.mallchat.common.user.domain.dto.SummaryInfoDTO;
import com.hyh.mallchat.common.user.domain.entity.Black;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.domain.vo.req.*;
import com.hyh.mallchat.common.user.domain.vo.resp.BadgesResp;
import com.hyh.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.hyh.mallchat.common.user.service.IBlackService;
import com.hyh.mallchat.common.user.service.IRoleService;
import com.hyh.mallchat.common.user.service.UserService;
import com.hyh.mallchat.common.user.service.adapter.UserAdapter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author CondiX
 * @since 2024-04-07
 */
@RestController
@Api(tags = "用户接口")
@RequestMapping("capi/user")
public class UserController {
    public static final long UID = 10028L;
    @Autowired
    private UserService userService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IBlackService blackService;
    @GetMapping("/userInfo")
    @ApiOperation("获取用户信息")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.getRequestInfo().getUid()));
    }
    @GetMapping("/public/summary/getUserInfo/batch")
    @ApiOperation("用户聚合信息-返回的代表需要刷新的")
    public ApiResult<List<SummaryInfoDTO>> getSummaryUserInfo(@Valid @RequestBody SummeryInfoReq req) {
        return ApiResult.success(userService.getSummaryUserInfo(req));
    }
    @GetMapping("/badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgesResp>> badges() {
        List<BadgesResp> badges = userService.badges(RequestHolder.getRequestInfo().getUid());
        return ApiResult.success(badges);
    }
    @GetMapping("/public/badges/batch")
    @ApiOperation("用户聚合信息-返回的代表需要刷新的")
    public ApiResult<List<ItemInfoDTO>> getItemInfo(@Valid @RequestBody ItemInfoReq req) {
        return ApiResult.success(userService.getItemInfo(req));
    }


    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> badge(@Valid @RequestBody BadgesReq req) {
        userService.wringBadge(RequestHolder.getRequestInfo().getUid(),req.getId());
        return ApiResult.success();
    }

    @PutMapping("/name")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.getRequestInfo().getUid(), req.getName());
        return ApiResult.success();
    }

    @PutMapping("/black")
    @ApiOperation("拉黑")
    public ApiResult<Void> black(@RequestBody BlackReq req) {
        Long uid = RequestHolder.getRequestInfo().getUid();
        boolean hasPower = roleService.hasPower(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasPower, "您咩有拉黑权限呢!!!!");
        Black blackUser = blackService.getBlackUser(req);
        blackService.back(req);
        return ApiResult.success();
    }

}

