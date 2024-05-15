package com.hyh.mallchat.common.user.dao;

import com.hyh.mallchat.common.user.domain.entity.Contact;
import com.hyh.mallchat.common.user.mapper.ContactMapper;
import com.hyh.mallchat.common.user.service.IContactService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
