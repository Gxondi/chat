package com.hyh.mallchat.common.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyh.mallchat.common.common.annotation.RedissonLock;
import com.hyh.mallchat.common.common.domain.enums.ApplyMsgStatusEnum;
import com.hyh.mallchat.common.common.domain.enums.ApplyStatusEnum;
import com.hyh.mallchat.common.common.domain.enums.ApplyTypeEnum;
import com.hyh.mallchat.common.common.domain.vo.req.CursorPageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.req.PageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.hyh.mallchat.common.common.domain.vo.resp.PageBaseResp;
import com.hyh.mallchat.common.common.event.UserApplyEvent;
import com.hyh.mallchat.common.common.utils.AssertUtil;
import com.hyh.mallchat.common.user.dao.UserApplyDao;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.dao.UserFriendDao;
import com.hyh.mallchat.common.user.domain.entity.RoomFriend;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.domain.entity.UserApply;
import com.hyh.mallchat.common.user.domain.entity.UserFriend;
import com.hyh.mallchat.common.user.domain.vo.req.FriendApplyReq;
import com.hyh.mallchat.common.user.domain.vo.req.FriendApproveReq;
import com.hyh.mallchat.common.user.domain.vo.req.FriendCheckReq;
import com.hyh.mallchat.common.user.domain.vo.req.FriendDeleteReq;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendApplyResp;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendCheckResp;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendResp;
import com.hyh.mallchat.common.user.domain.vo.resp.FriendUnReadCountResp;
import com.hyh.mallchat.common.user.service.IRoomService;
import com.hyh.mallchat.common.user.service.IUserFriendService;
import com.hyh.mallchat.common.user.service.adapter.UserFriendAdapter;
import com.hyh.mallchat.common.user.service.adapter.UserFriendApplyAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IUserFriendServiceImpl implements IUserFriendService {
    @Autowired
    private UserFriendDao userFriendDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserApplyDao userApplyDao;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private IRoomService roomService;

    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request) {
        //根据uid查询好友列表
        CursorPageBaseResp<UserFriend> friendPage = userFriendDao.getFriendPage(uid, request);
        //判空
        if (CollectionUtil.isEmpty(friendPage.getList())) {
            return CursorPageBaseResp.empty();
        }
        //筛选id
        List<Long> friendIds = friendPage.getList().stream().map(UserFriend::getFriendUid).collect(Collectors.toList());
        //根据好友id查询好友信息
        List<User> friendList = userDao.getFriendList(friendIds);
        //组装好后返回好友列表
        return CursorPageBaseResp.init(friendPage, UserFriendAdapter.buildFriendListResp(friendPage, friendList));
    }

    @Override
    public FriendCheckResp checkFriend(Long uid, FriendCheckReq request) {
        //根据uid和好友id查询好友列表
        List<UserFriend> friendList = userFriendDao.getFriendList(uid, request.getUidList());
        //1.筛选好友id
        Set<Long> friendIds = friendList.stream().map(UserFriend::getFriendUid).collect(Collectors.toSet());
        //2.组装返回结果
        List<FriendCheckResp.FriendCheck> friendCheckList = request.getUidList().stream().map(friendId -> {
            FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
            friendCheck.setUid(friendId);
            friendCheck.setIsFriend(friendIds.contains(friendId));
            return friendCheck;
        }).collect(Collectors.toList());
        return new FriendCheckResp(friendCheckList);
    }

    /**
     * 申请添加好友
     *
     * @param uid
     * @param request
     */
    @Override
    public void apply(Long uid, FriendApplyReq request) {
        //申请好友之前检查是否已经是好友了
        UserFriend friend = userFriendDao.getFriend(uid, request.getTargetUid());
        AssertUtil.isEmpty(friend, "已经是好友了");
        //是否已经申请过了（别人那边是待审批状态。）
        UserApply selfApproving = userApplyDao.getFriendApproving(uid, request.getTargetUid());
        if (Objects.nonNull(selfApproving)) {
            log.info("已经申请过了,uid:{},targetId:{}", uid, request.getTargetUid());
            return;
        }
        //对方是否申请了自己
        UserApply friendApproving = userApplyDao.getFriendApproving(request.getTargetUid(), uid);
        if (Objects.nonNull(friendApproving)) {
            //如果对方申请了自己，直接添加好友
            ((IUserFriendService) AopContext.currentProxy()).applyApprove(uid, new FriendApproveReq(friendApproving.getId()));
            return;
        }
        //添加好友申请
        UserApply insert = UserFriendApplyAdapter.buildFriendApply(uid, request);
        userApplyDao.save(insert);
        publisher.publishEvent(new UserApplyEvent(this, insert));
    }

    /**
     * 同意好友申请
     *
     * @param uid
     * @param request
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "uid")
    public void applyApprove(Long uid, FriendApproveReq request) {
        UserApply userApply = userApplyDao.getById(request.getApplyId());
        AssertUtil.isNotEmpty(userApply, "不存在的申请记录");
        AssertUtil.equal(userApply.getTargetId(), uid, "不存在的申请记录");
        AssertUtil.equal(userApply.getType(), ApplyStatusEnum.WAIT_APPROVE, "已经处理过了");
        //添加好友
        userApplyDao.agreeFriend(userApply.getId());
        //建立好友关系
        createFriend(uid, userApply.getUid());
        //创建聊天室
        RoomFriend roomFriend = roomService.createFriendRoom(Arrays.asList(uid, userApply.getUid()));
        //发送一条同意消息
        //TODO

    }

    @Override
    public FriendUnReadCountResp unRead(Long uid) {
        Integer unReadCount = userApplyDao.getUnReadCount(uid);
        return new FriendUnReadCountResp(unReadCount);
    }

    @Override
    public PageBaseResp<FriendApplyResp> getPageFriendApply(Long uid, PageBaseReq request) {
        IPage<UserApply> page = userApplyDao.friendApplyPage(uid, request.plusPage());
        if (CollectionUtil.isEmpty(page.getRecords())) {
            return PageBaseResp.empty();
        }
        List<Long> applyIds = page.getRecords().stream().map(UserApply::getId).collect(Collectors.toList());
        userApplyDao.readApply(uid,applyIds);
        return PageBaseResp.init(page, UserFriendApplyAdapter.buildFriendApplyResp(page.getRecords()));
    }

    @Override
    public void delete(Long uid, FriendDeleteReq request) {
        List<UserFriend> userFriend = userFriendDao.getUserFriend(uid, request.getTargetUid());
       if(CollectionUtil.isEmpty(userFriend)){
           log.info("不是好友关系，uid:{},targetUid:{}",uid,request.getTargetUid());
           return;
        }
        userFriendDao.removeByIds(userFriend);
        roomService.destroyFriendRoom(Arrays.asList(uid,request.getTargetUid()));
    }

    private void createFriend(Long uid, Long targetUid) {
        UserFriend userFriend1 = new UserFriend();
        userFriend1.setUid(uid);
        userFriend1.setFriendUid(targetUid);
        UserFriend userFriend2 = new UserFriend();
        userFriend2.setUid(targetUid);
        userFriend2.setFriendUid(uid);
        ArrayList<UserFriend> arrayList = new ArrayList<>();
        arrayList.add(userFriend1);
        arrayList.add(userFriend2);
        userFriendDao.saveBatch(arrayList);
    }
}
