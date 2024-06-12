package com.hyh.mallchat.common.user.service.impl;

import com.hyh.mallchat.common.common.annotation.RedissonLock;
import com.hyh.mallchat.common.common.domain.vo.resp.IdRespVO;
import com.hyh.mallchat.common.common.utils.AssertUtil;
import com.hyh.mallchat.common.user.dao.UserEmojiDao;
import com.hyh.mallchat.common.user.domain.entity.UserEmoji;
import com.hyh.mallchat.common.user.domain.vo.req.UserEmojiReq;
import com.hyh.mallchat.common.user.domain.vo.resp.UserEmojiResp;
import com.hyh.mallchat.common.user.service.IUserEmojiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IUserEmojiServiceImpl implements IUserEmojiService {
    @Autowired
    private UserEmojiDao userEmojiDao;
    @Override
    public List<UserEmojiResp> getEmojiList(Long uid) {

        return userEmojiDao.getEmojiList(uid).stream()
                .map(emoji->{
                    return UserEmojiResp.builder()
                            .id(emoji.getId())
                            .expressionUrl(emoji.getExpressionUrl())
                            .build();
                }).collect(Collectors.toList());
    }

    @Override
    @RedissonLock(key = "#uid")
    public IdRespVO addEmoji(Long uid, UserEmojiReq req) {
        Integer integer = userEmojiDao.countByUid(uid);
        AssertUtil.isFalse(integer > 30, "最多只能添加30个表情!!!!!");
        String expressionUrl = req.getExpressionUrl();
        //校验表情是否存在
        Integer count = userEmojiDao.lambdaQuery()
                .eq(UserEmoji::getExpressionUrl, expressionUrl)
                .eq(UserEmoji::getUid, uid)
                .count();
        AssertUtil.isFalse(count > 0, "当前表情已存在!!!!!");
        UserEmoji insert = UserEmoji.builder()
                .uid(uid)
                .expressionUrl(expressionUrl)
                .build();
        userEmojiDao.save(insert);
        return IdRespVO.id(insert.getId());
    }

    @Override
    public void deleteEmoji(Long uid, Long id) {
        UserEmoji userEmoji = userEmojiDao.getById(id);
        AssertUtil.isNotEmpty(userEmoji, "表情ID不能为空!!!!!");
        AssertUtil.equal(userEmoji.getUid(), uid, "小黑子，别人表情不是你能删的");
        userEmojiDao.removeById(id);
    }
}
