package com.hyh.mallchat.common.user.service.cache;

import com.hyh.mallchat.common.common.constant.RedisKey;
import com.hyh.mallchat.common.common.utils.RedisUtils;
import com.hyh.mallchat.common.user.dao.BlackDao;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.dao.UserRoleDao;
import com.hyh.mallchat.common.user.domain.entity.Black;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class UserCache {
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private BlackDao blackDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserSummaryCache userSummaryCache;

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
        collect.forEach((type, list) -> {
            result.put(type, list.stream().map(Black::getTarget).collect(Collectors.toSet()));
        });
        return result;
    }

    /**
     * 清除缓存
     *
     * @return
     */
    @CacheEvict(cacheNames = "user", key = "'blackList'")
    public HashMap<Integer, Set<String>> evictGetBlackMap() {
        return null;
    }

    /**
     * 获取用户修改时间
     */
    public List<Long> getUserModifyTime(List<Long> uidList) {
        List<String> collect = uidList.stream().map(uid -> RedisKey.getKey(RedisKey.USER_MODIFY_STRING, uid)).collect(Collectors.toList());
        return RedisUtils.mget(collect, Long.class);
    }

    /**
     * 获取用户信息
     * 组装成工具类
     */
    public Map<Long, User> getUserInfoBatch(Set<Long> uidSet) {
        //组装key
        List<String> key = uidSet.stream().map(uid -> RedisKey.getKey(RedisKey.USER_INFO_STRING, uid)).collect(Collectors.toList());
        //批量获取
        List<User> userList = RedisUtils.mget(key, User.class);
        //转换成map
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));
        //发现差集
        List<Long> needLoadUidList = uidSet.stream().filter(uid -> !userList.contains(uid)).collect(Collectors.toList());
        if (Objects.isNull(needLoadUidList)) {
            //数据库查询
            List<User> needLoadUserList = userDao.listByIds(needLoadUidList);
            //把差集转换成map
            Map<String, User> redisMap = needLoadUserList.stream().collect(Collectors.toMap(user -> RedisKey.getKey(RedisKey.USER_INFO_STRING, user.getId()), user -> user));
            RedisUtils.mset(redisMap, 5 * 60);
            userMap.putAll(needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity())));
        }
        //返回这个完整数据map（redis+database）
        return userMap;
    }
    public User getUserInfo(Long uid) {
        return getUserInfoBatch(Collections.singleton(uid)).get(uid);
    }

    /**
     * 用户信息变更
     */
    public void userInfoChange(Long uid) {
        delUserInfo(uid);
        //删除UserSummaryCache，前端下次懒加载的时候可以获取到最新的数据
        userSummaryCache.delete(uid);
        refreshUserModifyTime(uid);
    }

    /**
     * 刷新用户修改时间
     */
    private void refreshUserModifyTime(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_MODIFY_STRING, uid);
        RedisUtils.set(key, new Date().getTime());
    }

    /**
     * 删除用户信息
     */
    private void delUserInfo(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_INFO_STRING, uid);
        RedisUtils.del(key);
    }

    public boolean isOnline(Long uid) {
        String key = RedisKey.getKey(RedisKey.ONLINE_UID_ZET, uid);
        return RedisUtils.zIsMember(key, uid);
    }

    //用户下线
    public void offline(Long uid, Date optTime) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除上线线表
        RedisUtils.zRemove(onlineKey, uid);
        //更新上线表
        RedisUtils.zAdd(offlineKey, uid, optTime.getTime());
    }

    public void online(Long uid, Date optTime) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除离线表
        RedisUtils.zRemove(offlineKey, uid);
        //更新上线表
        RedisUtils.zAdd(onlineKey, uid, optTime.getTime());
    }


}
