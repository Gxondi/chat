package com.hyh.mallchat.common.user.service;

import com.hyh.mallchat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
