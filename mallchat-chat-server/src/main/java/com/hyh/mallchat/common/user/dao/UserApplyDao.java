package com.hyh.mallchat.common.user.dao;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyh.mallchat.common.common.domain.enums.ApplyMsgStatusEnum;
import com.hyh.mallchat.common.common.domain.enums.ApplyStatusEnum;
import com.hyh.mallchat.common.common.domain.enums.ApplyTypeEnum;
import com.hyh.mallchat.common.user.domain.entity.UserApply;
import com.hyh.mallchat.common.user.mapper.UserApplyMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户申请表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-11
 */
@Service
public class UserApplyDao extends ServiceImpl<UserApplyMapper, UserApply> {
    /**
     * 获取好友申请
     *
     * @param uid
     * @param targetUid
     * @return
     */
    public UserApply getFriendApproving(Long uid, Long targetUid) {
        return lambdaQuery().eq(UserApply::getUid, uid)
                .eq(UserApply::getTargetId, targetUid)
                .eq(UserApply::getType, ApplyStatusEnum.WAIT_APPROVE.getType())
                .eq(UserApply::getStatus, ApplyStatusEnum.WAIT_APPROVE.getType())
                .one();
    }

    /**
     * 获取未读消息数量
     *
     * @param targetId
     * @return
     */
    public Integer getUnReadCount(Long targetId) {
        return lambdaQuery().eq(UserApply::getTargetId, targetId)
                .eq(UserApply::getReadStatus, ApplyMsgStatusEnum.UNREAD.getType())
                .count();
    }


    public void agreeFriend(Long applyId) {
        lambdaUpdate().set(UserApply::getStatus, ApplyStatusEnum.APPROVED.getType())
                .eq(UserApply::getId, applyId)
                .update();
    }

    /**
     * 获取好友申请列表
     *
     * @param uid
     * @param page
     * @return
     */
    public Page friendApplyPage(Long uid, Page page) {
        return lambdaQuery().eq(UserApply::getUid, uid)
                .eq(UserApply::getStatus, ApplyTypeEnum.APPLY_FRIEND.getType())
                .orderByDesc(UserApply::getCreateTime)
                .page(page);
    }

    public void readApply(Long uid, List<Long> applyIds) {
        lambdaUpdate().eq(UserApply::getTargetId, uid)
                .eq(UserApply::getReadStatus, ApplyMsgStatusEnum.UNREAD.getType())
                .in(UserApply::getId, applyIds)
                .set(UserApply::getReadStatus, ApplyMsgStatusEnum.READ.getType())
                .update();
    }
}
