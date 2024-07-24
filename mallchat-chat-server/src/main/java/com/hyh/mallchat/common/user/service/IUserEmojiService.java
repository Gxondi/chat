package com.hyh.mallchat.common.user.service;

import com.hyh.mallchat.common.common.domain.vo.resp.IdRespVO;
import com.hyh.mallchat.common.user.domain.entity.UserEmoji;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyh.mallchat.common.user.domain.vo.req.UserEmojiReq;
import com.hyh.mallchat.common.user.domain.vo.resp.UserEmojiResp;

import java.util.List;

/**
 * <p>
 * 用户表情包 服务类
 * </p>
 *
 * @author CondiX
 * @since 2024-06-06
 */
public interface IUserEmojiService {

    List<UserEmojiResp> getEmojiList(Long uid);

    IdRespVO addEmoji(Long uid, UserEmojiReq req);

    void deleteEmoji(Long uid, Long id);
}
