package com.hyh.mallchat.common.user.service;

import com.hyh.mallchat.common.common.domain.enums.RoleEnum;
import com.hyh.mallchat.common.user.domain.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-04
 */
public interface IRoleService{
    public boolean hasPower(Long uid, RoleEnum roleEnum);
}
