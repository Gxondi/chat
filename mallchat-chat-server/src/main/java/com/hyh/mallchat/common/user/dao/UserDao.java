package com.hyh.mallchat.common.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
}
