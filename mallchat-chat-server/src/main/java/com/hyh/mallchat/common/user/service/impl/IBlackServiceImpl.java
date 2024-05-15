package com.hyh.mallchat.common.user.service.impl;

import com.hyh.mallchat.common.common.domain.enums.BlackEnum;
import com.hyh.mallchat.common.common.event.BlackEvent;
import com.hyh.mallchat.common.common.utils.AssertUtil;
import com.hyh.mallchat.common.user.dao.BlackDao;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.entity.Black;
import com.hyh.mallchat.common.user.domain.entity.IpInfo;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.domain.vo.req.BlackReq;
import com.hyh.mallchat.common.user.mapper.BlackMapper;
import com.hyh.mallchat.common.user.service.IBlackService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class IBlackServiceImpl implements IBlackService {
    @Autowired
    private BlackDao blackDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void back(BlackReq req) {
        Long uid = req.getUid();
        Black black = new Black();
        black.setId(uid);
        black.setType(BlackEnum.UID.getType());
        black.setTarget(uid.toString());
        blackDao.save(black);
        User user = userDao.getById(uid);
        blackIP(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getCreateIP).orElse(null));
        blackIP(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getUpdateIP).orElse(null));
        publisher.publishEvent(new BlackEvent(this, user));
    }

    @Override
    public Black getBlackUser(BlackReq req) {
        Long uid = req.getUid();
        Black blackUser = blackDao.getById(uid);
        return blackUser;
    }

    private void blackIP(String ip) {
        if(StringUtils.isBlank(ip)){
            return;
        }
        try {
            Black black = new Black();
            black.setType(BlackEnum.IP.getType());
            black.setTarget(ip);
            blackDao.save(black);
        } catch (Exception e) {

        }
    }
}
