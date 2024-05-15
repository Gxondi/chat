package com.hyh.mallchat.common.user.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.hyh.mallchat.common.common.annotation.RedissonLock;
import com.hyh.mallchat.common.common.domain.enums.ItemEnum;
import com.hyh.mallchat.common.common.domain.enums.ItemTypeEnum;
import com.hyh.mallchat.common.common.domain.enums.RoleEnum;
import com.hyh.mallchat.common.common.event.RegisterEvent;
import com.hyh.mallchat.common.common.exception.BusinessException;
import com.hyh.mallchat.common.common.exception.CommonErrorEnum;
import com.hyh.mallchat.common.common.utils.AssertUtil;
import com.hyh.mallchat.common.user.dao.ItemConfigDao;
import com.hyh.mallchat.common.user.dao.UserBackpackDao;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.entity.ItemConfig;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.domain.entity.UserBackpack;
import com.hyh.mallchat.common.user.domain.vo.resp.BadgesResp;
import com.hyh.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.hyh.mallchat.common.user.service.UserService;
import com.hyh.mallchat.common.user.service.adapter.UserAdapter;
import com.hyh.mallchat.common.user.service.cache.ItemCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    private ItemCache itemCache;

    @Autowired
    private ItemConfigDao itemConfigDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 注册事件是需要被人监听的
     * 一旦该方法被调用执行，监听此事件的方法要做出反应比如主动发放积分，勋章等等一系列的任务
     * @param insert
     * @return
     */
    @Override
    @Transactional
    public Long register(User insert) {
        boolean save = userDao.save(insert);
        //TODO 用户注册事件
        /**
         * 事件的订阅者想知道是从那个类发出来的（this）
         * 使用springboot的事件，是不可靠的发送。
         * 如果在事务内发送的MQ，如果方法回查，注册是空的，事务还没有提交成功
         */
        applicationEventPublisher.publishEvent(new RegisterEvent(this,insert));
        return insert.getId();
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        //获取用户信息
        User user = userDao.getUserInfo(uid);
        //获取用户背包信息
        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());

        return UserAdapter.buildUserInfoResp(user, countByValidItemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#uid")
    public void modifyName(Long uid, String name) {
        User oldUser = userDao.getByName(name);
        //判断名字是不是重复
        AssertUtil.isEmpty(oldUser, "名字已经被占用，请换个名字~");
        //获取item
        UserBackpack modifyNameItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(modifyNameItem, "改名卡不足");
        //修改名字
        boolean success = userBackpackDao.useItem(modifyNameItem);
        if (success) {
            userDao.modifyName(uid, name);
        }
    }

    /**
     * 用户徽章列表
     * 徽章列表是大家都要获取的，比较稳定，这里可以做本地缓存。不需要远端io操作
     * 如果要经常两边同步，可以考虑使用redis中心化缓存
     *
     * @param uid
     * @return
     */
    @Override
    public List<BadgesResp> badges(Long uid) {
        //获取徽章列表
        List<ItemConfig> badges = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        //查询用户拥有的徽章
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uid, badges.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        //获取用户
        User user = userDao.getById(uid);
        //组装
        return UserAdapter.buildBadgesResp(badges, backpacks, user);
    }

    /**
     * 佩戴徽章
     *
     * @param uid
     * @param itemId
     */
    @Override
    public void wringBadge(Long uid, Long itemId) {
        UserBackpack item = userBackpackDao.getFirstValidItem(uid, itemId);
        AssertUtil.isNotEmpty(item, "还没有获得此徽章，快去获取吧");
        ItemConfig itemConfig = itemConfigDao.getById(item.getItemId());
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "只有徽章才能佩戴");
        userDao.wringBadge(uid, item.getItemId());
    }



}
