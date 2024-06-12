package com.hyh.mallchat.common.chat.service.strategy.msg;

import com.hyh.mallchat.common.common.exception.CommonErrorEnum;
import com.hyh.mallchat.common.common.utils.AssertUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class MsgHandlerFactory {

    private static final Map<Integer,AbstractMsgHandler> STRATEGY_MAP = new HashMap<>();
    /**
     * 注册消息处理器
     * @param code
     * @param strategy
     */
    public static void register(Integer code, AbstractMsgHandler strategy) {
        STRATEGY_MAP.put(code, strategy);
    }
    /**
     * 获取消息处理器
     * @param code
     * @return
     */
    public static AbstractMsgHandler getStrategyNoNull(Integer code) {
        AbstractMsgHandler strategy = STRATEGY_MAP.get(code);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }

}
