package com.hyh.mallchat.common.user.service;

import com.hyh.mallchat.common.user.domain.entity.Black;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyh.mallchat.common.user.domain.vo.req.BlackReq;

/**
 * <p>
 * 黑名单 服务类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-04
 */
public interface IBlackService{

    void back(BlackReq req);

    Black getBlackUser(BlackReq req);

}
