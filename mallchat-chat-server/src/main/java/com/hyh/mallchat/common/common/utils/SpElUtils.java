package com.hyh.mallchat.common.common.utils;

import org.apache.catalina.core.StandardContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

public class SpElUtils {
    //创建SpEl解析器
    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();
    //获取方法参数名的类
    private static final DefaultParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    //method -》 public void com.hyh.mallchat.common.user.service.impl.UserServiceImpl.modifyName(java.lang.Long,java.lang.String)
    //args -》 20001 凌晨
    public static String parseSpEl(Method method, Object[] args, String key) {
        //解析参数名
        //["uid", "name"]
        String[] parameterNames = Optional.ofNullable(PARAMETER_NAME_DISCOVERER.getParameterNames(method)).orElse(new String[]{});
        EvaluationContext context = new StandardEvaluationContext();//el解析需要的上下文对象
        for (int i = 0; i < parameterNames.length; i++) {
            //参数名对照参数值
            //parameterNames ["uid", "name"]
            //args       -》 [20001, 凌晨]
            context.setVariable(parameterNames[i],args[i]);
        }
        //根据上述，解析表达式，生成表达式
        Expression expression = SPEL_EXPRESSION_PARSER.parseExpression(key);
        //返回key
        return expression.getValue(context,String.class);
    }
    public static String getMethodKey(Method method) {
        //getDeclaringClass 获取 class名
        //这一步是为了确保方法不一样
        return method.getDeclaringClass()+"#"+method.getName();
    }
}
