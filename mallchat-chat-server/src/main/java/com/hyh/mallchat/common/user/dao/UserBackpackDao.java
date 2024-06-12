package com.hyh.mallchat.common.user.dao;

import com.hyh.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.hyh.mallchat.common.user.domain.entity.UserBackpack;
import com.hyh.mallchat.common.user.mapper.UserBackpackMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-04-18
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {
    /**
     * 根据用户id和物品id获取背包中物品数量
     *
     * @param uid
     * @param itemId
     * @return
     */
    public Integer getCountByValidItemId(Long uid, Long itemId) {

        return lambdaQuery().eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .count();
    }
    /**
     * 获取用户背包中时间最久的有效的物品
     * @param uid
     * @param itemId
     * @return
     */
    public UserBackpack getFirstValidItem(Long uid, Long itemId) {
        return lambdaQuery().eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .orderByAsc(UserBackpack::getId)
                .last("limit 1")
                .one();
    }

    /**
     * 使用物品
     * 更新物品状态
     * @param modifyNameItem
     * @return
     */
    public boolean useItem(UserBackpack modifyNameItem) {
        return lambdaUpdate()
                .eq(UserBackpack::getId,modifyNameItem.getId())
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .set(UserBackpack::getStatus, YesOrNoEnum.YES.getStatus())
                .update();

    }

    public List<UserBackpack> getByItemIds(Long uid, List<Long> itemIds) {
        return lambdaQuery().eq(UserBackpack::getUid, uid)
                .in(UserBackpack::getItemId, itemIds)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus()) //有效徽章
                .list();
    }

    public List<UserBackpack> getByItemIds(List<Long> uid, List<Long> itemIds) {
        return lambdaQuery().in(UserBackpack::getUid, uid)
                .in(UserBackpack::getItemId, itemIds)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus()) //有效徽章
                .list();
    }
    public UserBackpack getIdempotent(String idempotent) {
        return lambdaQuery().eq(UserBackpack::getIdempotent,idempotent).one();
    }
}
