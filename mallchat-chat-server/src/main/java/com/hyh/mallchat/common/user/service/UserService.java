package com.hyh.mallchat.common.user.service;

import com.hyh.mallchat.common.common.domain.enums.RoleEnum;
import com.hyh.mallchat.common.user.domain.dto.ItemInfoDTO;
import com.hyh.mallchat.common.user.domain.dto.SummaryInfoDTO;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyh.mallchat.common.user.domain.vo.req.ItemInfoReq;
import com.hyh.mallchat.common.user.domain.vo.req.SummeryInfoReq;
import com.hyh.mallchat.common.user.domain.vo.resp.BadgesResp;
import com.hyh.mallchat.common.user.domain.vo.resp.UserInfoResp;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author CondiX
 * @since 2024-04-07
 */
public interface UserService {

    Long register(User insert);

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, String name);

    List<BadgesResp> badges(Long uid);

    void wringBadge(Long uid, Long id);

    List<SummaryInfoDTO> getSummaryUserInfo(SummeryInfoReq req);

    List<ItemInfoDTO> getItemInfo(ItemInfoReq req);
}
