package com.hyh.mallchat.common.user.dao;

import com.hyh.mallchat.common.common.domain.enums.RoleEnum;
import com.hyh.mallchat.common.user.domain.entity.Role;
import com.hyh.mallchat.common.user.mapper.RoleMapper;
import com.hyh.mallchat.common.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-04
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role>{
    public List<Role> getRoleSet(Long uid) {
        return lambdaQuery().eq(Role::getId, uid).list();
    }
}
