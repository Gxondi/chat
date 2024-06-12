package com.hyh.mallchat.common.chat.service.strategy.mark;

import com.hyh.mallchat.common.common.exception.CommonErrorEnum;
import com.hyh.mallchat.common.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 策略工程构建
 */
public class MsgMarkFactory {
    private static final Map<Integer, AbstractMsgMarkHandler> strategyMap = new HashMap<>();

    public static void register(AbstractMsgMarkHandler abstractMsgMarkHandler, Integer type) {
        strategyMap.put(type, abstractMsgMarkHandler);
    }

    public static AbstractMsgMarkHandler getStrategyNoNull(Integer type) {
        AbstractMsgMarkHandler strategy = strategyMap.get(type);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }
}
