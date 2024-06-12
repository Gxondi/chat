package com.hyh.mallchat.common.chat.dao;

import com.hyh.mallchat.common.chat.domain.entity.Contact;
import com.hyh.mallchat.common.chat.mapper.ContactMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 会话列表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-15
 */
@Service
public class ContactDao extends ServiceImpl<ContactMapper, Contact> {

    public void refreshActiveTime(Long roomId, List<Long> memeberList, Long id1, Date createTime) {
       baseMapper.refreshOrCreateActiveTime(roomId, memeberList, id1, createTime);
    }

    public Contact get(Long uid, Long roomId) {
        return lambdaQuery()
                .eq(Contact::getUid, uid)
                .eq(Contact::getRoomId, roomId)
                .one();
    }
}
