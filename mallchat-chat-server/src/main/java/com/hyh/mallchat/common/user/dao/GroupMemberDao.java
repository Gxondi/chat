package com.hyh.mallchat.common.user.dao;

import com.hyh.mallchat.common.user.domain.entity.GroupMember;
import com.hyh.mallchat.common.user.mapper.GroupMemberMapper;
import com.hyh.mallchat.common.user.service.IGroupMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群成员表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-15
 */
@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember>{

}
