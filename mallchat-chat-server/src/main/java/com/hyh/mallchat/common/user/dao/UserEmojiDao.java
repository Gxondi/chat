package com.hyh.mallchat.common.user.dao;

import com.hyh.mallchat.common.user.domain.entity.UserEmoji;
import com.hyh.mallchat.common.user.mapper.UserEmojiMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表情包 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-06-06
 */
@Service
public class UserEmojiDao extends ServiceImpl<UserEmojiMapper, UserEmoji> {

    public List<UserEmoji> getEmojiList(Long uid) {
        return lambdaQuery()
                .eq(UserEmoji::getUid,uid)
                .list();

    }

    public Integer countByUid(Long uid) {
        return lambdaQuery()
                .eq(UserEmoji::getUid,uid)
                .count();
    }
}
