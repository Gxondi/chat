package com.hyh.mallchat.common.chat.dao;

import com.hyh.mallchat.common.chat.domain.entity.GroupMember;
import com.hyh.mallchat.common.chat.domain.entity.RoomGroup;
import com.hyh.mallchat.common.chat.mapper.GroupMemberMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public GroupMember getGroupMember(Long groupId ,Long uid ) {
        return lambdaQuery().eq(GroupMember::getGroupId, groupId).eq(GroupMember::getUid, uid).one();
    }

    public List<Long> getMemberIdsByRoomId(Long uid) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getUid, uid)
                .select(GroupMember::getUid)
                .list();
        return list.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }


}
