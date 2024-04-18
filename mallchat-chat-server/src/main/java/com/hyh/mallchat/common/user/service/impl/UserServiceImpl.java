package com.hyh.mallchat.common.user.service.impl;

import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Override
    @Transactional
    public Long register(User insert) {
        boolean save = userDao.save(insert);
        //TODO 用户注册事件
        return insert.getId();
    }
}
