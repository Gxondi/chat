package com.hyh.mallchat.common.user.dao;

import com.hyh.mallchat.common.user.domain.entity.Message;
import com.hyh.mallchat.common.user.mapper.MessageMapper;
import com.hyh.mallchat.common.user.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-15
 */
@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {

}
