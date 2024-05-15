package com.hyh.mallchat.common.user.dao;

import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-04-07
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

    public User getUserByOpenId(String openId) {
      return lambdaQuery().eq(User::getOpenId, openId).one();
    }

    public User getUserInfo(Long uid) {
        return lambdaQuery().eq(User::getId, uid).one();
    }

    public User getByName(String name) {
        return lambdaQuery().eq(User::getName, name).one();
    }
    public void modifyName(Long uid, String name) {
        lambdaUpdate()
                .eq(User::getId,uid)
                .set(User::getName,name)
                .update();
    }

    public void wringBadge(Long uid, Long itemId) {
        lambdaUpdate()
                .eq(User::getId,uid)
                .set(User::getItemId,itemId)
                .update();
    }

    public void updateStatus(Long id) {
        lambdaUpdate()
                .eq(User::getId,id)
                .set(User::getStatus, 1)
                .update();
    }

    public List<User> getFriendList(List<Long> friendIds) {
        return lambdaQuery()
                .in(User::getId, friendIds)
                .select(User::getId, User::getName, User::getAvatar, User::getActiveStatus)
                .list();
    }
}
