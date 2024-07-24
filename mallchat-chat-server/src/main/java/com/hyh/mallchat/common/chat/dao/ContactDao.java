package com.hyh.mallchat.common.chat.dao;

import com.hyh.mallchat.common.chat.domain.entity.Contact;
import com.hyh.mallchat.common.chat.domain.entity.Message;
import com.hyh.mallchat.common.chat.domain.vo.req.ChatMessageReadReq;
import com.hyh.mallchat.common.chat.mapper.ContactMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyh.mallchat.common.common.domain.vo.req.CursorPageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.hyh.mallchat.common.common.utils.CursorUtils;
import io.swagger.models.auth.In;
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


    public CursorPageBaseResp<Contact> getPage(Long uid, CursorPageBaseReq cursorReq) {
        CursorPageBaseResp<Contact> cursorPageByMysql = CursorUtils.getCursorPageByMysql(this, cursorReq, wrapper -> {
            wrapper.eq(Contact::getUid, uid);
        }, Contact::getActiveTime);
        return cursorPageByMysql;
    }

    public List<Contact> getByRoomIds(Long uid, List<Long> roomIds) {
        List<Contact> list = lambdaQuery()
                .eq(Contact::getUid, uid)
                .in(Contact::getRoomId, roomIds)
                .list();
        return list;
    }

    public CursorPageBaseResp<Contact> getReadPage(Message message, ChatMessageReadReq req) {
        return CursorUtils.getCursorPageByMysql(this,req,wrapper ->{
            wrapper.eq(Contact::getRoomId,message.getRoomId())
                    .ne(Contact::getUid,message.getFromUid())
                    .ge(Contact::getReadTime,message.getCreateTime());
        },Contact::getReadTime);
    }

    public CursorPageBaseResp<Contact> getUnReadPage(Message message, ChatMessageReadReq req) {
        return CursorUtils.getCursorPageByMysql(this,req,wrapper ->{
            wrapper.eq(Contact::getRoomId,message.getRoomId())
                    .ne(Contact::getUid,message.getFromUid())
                    .le(Contact::getReadTime,message.getCreateTime());
        },Contact::getReadTime);
    }

    public Integer getTotal(Long roomId) {
        Integer count = lambdaQuery().eq(Contact::getRoomId, roomId)
                .count();
        return count;

    }

    public Integer getReadCount(Message message) {
        Integer count = lambdaQuery()
                .eq(Contact::getRoomId, message.getRoomId())
                .ne(Contact::getUid, message.getFromUid())
                .gt(Contact::getReadTime, message.getCreateTime())
                .count();
        return count;
    }
}
