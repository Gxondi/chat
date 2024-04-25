package com.hyh.mallchat.common.user.service;

import com.hyh.mallchat.common.common.domain.enums.IdempotentEnum;
import com.hyh.mallchat.common.user.domain.entity.UserBackpack;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author CondiX
 * @since 2024-04-18
 */
public interface IUserBackpackService {
    /**
     * 给用户发放物品
     * @param uid 用户id
     * @param itemId 物品id
     * @param idempotentEnum 幂等性类型
     * @param businessId 上层业务发送的唯一标识
     */
    void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum,String businessId);

}
