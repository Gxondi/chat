package com.hyh.mallchat.common.user.service.impl;

import com.hyh.mallchat.common.common.domain.enums.RoleEnum;
import com.hyh.mallchat.common.user.dao.RoleDao;
import com.hyh.mallchat.common.user.domain.entity.Role;
import com.hyh.mallchat.common.user.service.IRoleService;
import com.hyh.mallchat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class IRoleServiceImpl implements IRoleService {

    @Autowired
    private UserCache userCache;

    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        Set<Long> roleSet = userCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains(roleEnum.getType());
    }
    public boolean isAdmin(Set<Long> roleSet){
       return roleSet.contains(RoleEnum.ADMIN.getType());
    }
}
