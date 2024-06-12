package com.hyh.mallchat.common.chat.mapper;

import com.hyh.mallchat.common.chat.domain.entity.Contact;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 会话列表 Mapper 接口
 * </p>
 *
 * @author CondiX
 * @since 2024-05-15
 */
public interface ContactMapper extends BaseMapper<Contact> {

    void refreshOrCreateActiveTime(@Param("rooId") Long roomId, @Param("memberList") List<Long> memberList,@Param("msgId") Long msgId,@Param("createTime") Date createTime);
}
