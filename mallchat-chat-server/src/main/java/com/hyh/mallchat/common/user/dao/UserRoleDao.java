package com.hyh.mallchat.common.user.dao;

import com.hyh.mallchat.common.user.domain.entity.Role;
import com.hyh.mallchat.common.user.domain.entity.UserRole;
import com.hyh.mallchat.common.user.mapper.UserRoleMapper;
import com.hyh.mallchat.common.user.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-04
 */
@Service
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole>{

    public List<UserRole> getRoleSet(Long uid) {
        return lambdaQuery().eq(UserRole::getUid, uid).list();
    }
}
