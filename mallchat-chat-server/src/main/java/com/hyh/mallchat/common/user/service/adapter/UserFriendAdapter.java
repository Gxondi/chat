package com.hyh.mallchat.common.user.service.adapter;


import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.domain.entity.UserFriend;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendResp;
import java.util.*;
import java.util.stream.Collectors;

public class UserFriendAdapter {

    public static List<FriendResp> buildFriendListResp(CursorPageBaseResp<UserFriend> friendPage, List<User> friendList) {
        //id映射用户信息，方便后续使用
        Map<Long, User> userMap = friendList.stream().collect(Collectors.toMap(User::getId, user -> user));
        //查询出来的好友实体-》组装好友列表返回类
        List<FriendResp> friendRespList = friendPage.getList().stream().map(userFriend -> {
            FriendResp friendResp = new FriendResp();
            friendResp.setUid(userFriend.getFriendUid());
            User user = userMap.get(userFriend.getFriendUid());
            if (Objects.nonNull(user)) {
                friendResp.setActiveStatus(user.getActiveStatus());
                friendResp.setAvatar(user.getAvatar());
                friendResp.setName(user.getName());
            }
            return friendResp;
        }).collect(Collectors.toList());
        return friendRespList;
    }

    public static void deleteFriend(Long uid, Long uid1, Long targetUid) {
    }
}
