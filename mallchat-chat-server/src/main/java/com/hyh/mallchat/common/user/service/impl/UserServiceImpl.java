package com.hyh.mallchat.common.user.service.impl;

import com.hyh.mallchat.common.common.annotation.RedissonLock;
import com.hyh.mallchat.common.common.domain.enums.ItemEnum;
import com.hyh.mallchat.common.common.domain.enums.ItemTypeEnum;
import com.hyh.mallchat.common.common.event.RegisterEvent;
import com.hyh.mallchat.common.common.utils.AssertUtil;
import com.hyh.mallchat.common.user.dao.ItemConfigDao;
import com.hyh.mallchat.common.user.dao.UserBackpackDao;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.dto.ItemInfoDTO;
import com.hyh.mallchat.common.user.domain.dto.SummaryInfoDTO;
import com.hyh.mallchat.common.user.domain.entity.ItemConfig;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.domain.entity.UserBackpack;
import com.hyh.mallchat.common.user.domain.vo.req.ItemInfoReq;
import com.hyh.mallchat.common.user.domain.vo.req.SummeryInfoReq;
import com.hyh.mallchat.common.user.domain.vo.resp.BadgesResp;
import com.hyh.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.hyh.mallchat.common.user.service.UserService;
import com.hyh.mallchat.common.user.service.adapter.UserAdapter;
import com.hyh.mallchat.common.user.service.cache.ItemCache;
import com.hyh.mallchat.common.user.service.cache.UserSummaryCache;
import com.hyh.mallchat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    @Autowired
    private UserCache userCache;
    @Autowired
    private UserSummaryCache userSummaryCache;

    /**
     * 注册事件是需要被人监听的
     * 一旦该方法被调用执行，监听此事件的方法要做出反应比如主动发放积分，勋章等等一系列的任务
     *
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
        applicationEventPublisher.publishEvent(new RegisterEvent(this, insert));
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
            //删除缓存，刷新更新时间
            userCache.userInfoChange(uid);
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
        userCache.userInfoChange(uid);
    }

    /**
     * 获取用户信息
     *
     * @param req
     * @return
     */
    @Override
    public List<SummaryInfoDTO> getSummaryUserInfo(SummeryInfoReq req) {
        //获取需要更新的用户
        List<Long> uidList = getNeedSyncUidList(req.getInfoReq());
        //加载用户信息
        Map<Long, SummaryInfoDTO> batch = userSummaryCache.getBatch(uidList);
        return req.getInfoReq()
                .stream()
                .map(a -> batch.containsKey(a.getUid()) ? batch.get(a.getUid()) : SummaryInfoDTO.skip(a.getUid()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    /**
     * 获取物品信息
     *
     * @param req
     * @return
     */
    @Override
    public List<ItemInfoDTO> getItemInfo(ItemInfoReq req) {
        return req.getInfoReq().stream()
                .map(a -> {
                    ItemConfig itemConfig = itemCache.getByItemId(a.getItemId());
                    if (Objects.nonNull(itemConfig) && Objects.isNull(a.getLastModifyTime()) || a.getLastModifyTime() >= itemConfig.getUpdateTime().getTime()) {
                        return ItemInfoDTO.skip(a.getItemId());
                    }
                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
                    itemInfoDTO.setItemId(a.getItemId());
                    itemInfoDTO.setDescribe(itemConfig.getDescribe());
                    itemInfoDTO.setImg(itemConfig.getImg());
                    return itemInfoDTO;
                }).collect(Collectors.toList());
    }

    /**
     * 获取需要更新的用户id
     *
     * @param reqList
     * @return
     */
    private List<Long> getNeedSyncUidList(List<SummeryInfoReq.infoReq> reqList) {
        //先去redis缓存中找
        List<Long> userModifyTime = userCache.getUserModifyTime(reqList.stream().map(SummeryInfoReq.infoReq::getUid).collect(Collectors.toList()));
        //找到需要更新的用户
        List<Long> needSyncUidList = new ArrayList<>();
        for (int i = 0; i < reqList.size(); i++) {
            Long modifyTime = userModifyTime.get(i);
            SummeryInfoReq.infoReq infoReq = reqList.get(i);
            if (Objects.isNull(infoReq.getLastModifyTime()) || (Objects.nonNull(modifyTime) && infoReq.getLastModifyTime() < modifyTime)) {
                needSyncUidList.add(infoReq.getUid());
            }
        }
        return needSyncUidList;
    }


}
