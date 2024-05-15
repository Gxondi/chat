package com.hyh.mallchat.common.user.service.cache;

import com.hyh.mallchat.common.user.dao.BlackDao;
import com.hyh.mallchat.common.user.dao.UserRoleDao;
import com.hyh.mallchat.common.user.domain.entity.Black;
import com.hyh.mallchat.common.user.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class UserCache {
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private BlackDao blackDao;
    /**
     * 获取该用户的权限列表
     */
    @Cacheable(cacheNames = "role", key = "'roles:'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> roleSet = userRoleDao.getRoleSet(uid);
        Set<Long> collect = roleSet.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        return collect;
    }
    @Cacheable(cacheNames = "user", key = "'blackList'")
    public HashMap<Integer, Set<String>> getBlackMap() {
        List<Black> blackList = blackDao.list();
        // 按照type分组 拉黑目标类型 1.ip 2uid
        Map<Integer, List<Black>> collect = blackList.stream().collect(Collectors.groupingBy(Black::getType));
        HashMap<Integer, Set<String>> result = new HashMap<>();
        //再根据type获取对应的拉黑目标
        collect.forEach((type, list) -> { result.put(type, list.stream().map(Black::getTarget).collect(Collectors.toSet()));});
        return result;
    }

    /**
     * 清除缓存
     * @return
     */
    @CacheEvict(cacheNames = "user", key = "'blackList'")
    public HashMap<Integer, Set<String>> evictGetBlackMap(){
        return null;
    }
}
