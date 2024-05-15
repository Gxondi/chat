package com.hyh.mallchat.common.user.dao;

import com.hyh.mallchat.common.user.domain.entity.Black;
import com.hyh.mallchat.common.user.mapper.BlackMapper;
import com.hyh.mallchat.common.user.service.IBlackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-04
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black>{

}
