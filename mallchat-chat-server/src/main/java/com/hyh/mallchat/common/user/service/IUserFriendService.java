package com.hyh.mallchat.common.user.service;

import com.hyh.mallchat.common.common.domain.vo.req.CursorPageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.req.PageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.hyh.mallchat.common.common.domain.vo.resp.PageBaseResp;
import com.hyh.mallchat.common.user.domain.entity.UserFriend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyh.mallchat.common.user.domain.vo.req.FriendApplyReq;
import com.hyh.mallchat.common.user.domain.vo.req.FriendApproveReq;
import com.hyh.mallchat.common.user.domain.vo.req.FriendCheckReq;
import com.hyh.mallchat.common.user.domain.vo.req.FriendDeleteReq;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendApplyResp;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendCheckResp;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendResp;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendUnReadCountResp;

/**
 * <p>
 * 用户联系人表 服务类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-11
 */
public interface IUserFriendService{

    /**
     * 好友列表
     *
     * @param uid
     * @param request
     * @return
     */
    CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request);
    /**
     * 检查是否是好友
     *
     * @param uid
     * @param request
     * @return
     */

    FriendCheckResp checkFriend(Long uid, FriendCheckReq request);
    /**
     * 申请添加好友
     *
     * @param uid
     * @param request
     */
    void apply(Long uid, FriendApplyReq request);
    /**
     * 同意好友申请
     *
     * @param uid
     * @param friendApproveReq
     */
    void applyApprove(Long uid, FriendApproveReq friendApproveReq);

    FriendUnReadCountResp unRead(Long uid);

    PageBaseResp<FriendApplyResp> getPageFriendApply(Long uid, PageBaseReq request);


    void delete(Long uid, FriendDeleteReq request);
}
