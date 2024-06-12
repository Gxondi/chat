package com.hyh.mallchat.common.user.service.adapter;


import com.hyh.mallchat.common.common.domain.enums.ApplyMsgStatusEnum;
import com.hyh.mallchat.common.common.domain.enums.ApplyStatusEnum;
import com.hyh.mallchat.common.common.domain.enums.ApplyTypeEnum;
import com.hyh.mallchat.common.common.domain.vo.resp.PageBaseResp;
import com.hyh.mallchat.common.user.domain.entity.UserApply;
import com.hyh.mallchat.common.user.domain.vo.req.FriendApplyReq;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendApplyResp;
import com.hyh.mallchat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class UserFriendApplyAdapter {

    public static List<FriendApplyResp> buildFriendApplyResp(List<UserApply> records) {
        List<FriendApplyResp> collect = records.stream().map(item -> {
            FriendApplyResp friendApplyResp = new FriendApplyResp();
            friendApplyResp.setApplyId(item.getId());
            friendApplyResp.setUid(item.getUid());
            friendApplyResp.setType(item.getType());
            friendApplyResp.setMsg(item.getMsg());
            friendApplyResp.setStatus(item.getStatus());
            return friendApplyResp;
        }).collect(Collectors.toList());
        return collect;
    }

    public static UserApply buildFriendApply(Long uid, FriendApplyReq request) {
        UserApply userApply = new UserApply();
        userApply.setUid(uid);
        userApply.setTargetId(request.getTargetUid());
        userApply.setMsg(request.getMsg());
        userApply.setType(ApplyTypeEnum.APPLY_FRIEND.getType());
        userApply.setStatus(ApplyStatusEnum.WAIT_APPROVE.getType());
        userApply.setReadStatus(ApplyMsgStatusEnum.UNREAD.getType());
        return userApply;
    }
}

