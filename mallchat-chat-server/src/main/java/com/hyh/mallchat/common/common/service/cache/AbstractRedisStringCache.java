package com.hyh.mallchat.common.common.service.cache;

import cn.hutool.core.collection.CollectionUtil;

import com.hyh.mallchat.common.common.utils.RedisUtils;
import org.springframework.data.util.Pair;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 批量缓存
 * 复用缓存接口定义基本能力
 * 把缓存的基本能力抽象出来
 * 把变量交给子类去实现
 * @param <IN>
 * @param <OUT>
 */
public abstract class AbstractRedisStringCache<IN, OUT> implements BatchCache<IN, OUT>{
    Class<OUT> outClass;

    /**
     * 获取子类的泛型类型
     * 通过反射获取子类的泛型类型
     * getGenericSuperclass 获得带有泛型的父类
     * ParameterizedType参数化类型，即泛型
     * getActualTypeArguments返回的是一个数组，泛型参数的实际类型可能有多个
     * getActualTypeArguments()[1]获取第二个泛型类型OUT
     */
    protected AbstractRedisStringCache(){
        this.outClass = (Class<OUT>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }
    protected abstract Long getExpireSeconds();
    /**
     * 缓存前缀是根据调用方法是不同的
     */
    protected abstract String getKey(IN req);
    /**
     * 根据方法的调用加载数据是不同的
     */
    protected abstract Map<IN,OUT> load(List<IN> req);
    @Override
    public OUT get(IN req) {
        return getBatch(Collections.singletonList(req)).get(req);
    }
//    public Map<Long, User> getUserInfo(Set<Long> uidSet) {
//        //组装key
//        List<String> key = uidSet.stream().map(uid -> RedisKey.getKey(RedisKey.USER_INFO_STRING, uid)).collect(Collectors.toList());
//        //批量获取
//        List<User> userList = RedisUtils.mget(key, User.class);
//        //转换成map
//        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));
//        //发现差集
//        List<Long> needLoadUidList = uidSet.stream().filter(uid -> !userList.contains(uid)).collect(Collectors.toList());
//        if (Objects.isNull(needLoadUidList)) {
//            //数据库查询
//            List<User> needLoadUserList = userDao.listByIds(needLoadUidList);
//            //把差集转换成map
//            Map<String, User> redisMap = needLoadUserList.stream().collect(Collectors.toMap(user -> RedisKey.getKey(RedisKey.USER_INFO_STRING, user.getId()), user -> user));
//            RedisUtils.mset(redisMap, 5 * 60);
//            userMap.putAll(needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity())));
//        }
//        //返回这个完整数据map（redis+database）
//        return userMap;
//    }
    @Override
    public Map<IN, OUT> getBatch(List<IN> req) {
        //防御性编程
        if(req.isEmpty()){
            return new HashMap<>();
        }
        //去重
        req = req.stream().distinct().collect(Collectors.toList());
        //获取key
        List<String> key = req.stream().map(this::getKey).collect(Collectors.toList());
        //批量获取
        List<OUT> outList = RedisUtils.mget(key, outClass);
        //差集计算
        List<IN> loadReqs = new ArrayList<>();
        for (int i = 0; i < outList.size(); i++) {
            if(Objects.isNull(outList.get(i))){
                loadReqs.add(req.get(i));
            }
        }
        Map<IN, OUT> load = new HashMap<>();
        if(CollectionUtil.isNotEmpty(loadReqs)){
            load =  load(loadReqs);
            Map<String, OUT> collect = load.entrySet()
                    .stream()
                    .map(a -> Pair.of(getKey(a.getKey()), a.getValue()))
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
            RedisUtils.mset(collect, getExpireSeconds());
        }
        Map<IN, OUT> resultMap = new HashMap<>();
        for (int i = 0; i < req.size(); i++) {
            IN in = req.get(i);
            OUT out = Optional.ofNullable(outList.get(i)).orElse(load.get(in));
            resultMap.put(in, out);
        }
        return resultMap;
    }
    @Override
    public void delete(IN req) {
        deleteBatch(Collections.singletonList(req));
    }

    @Override
    public void deleteBatch(List<IN> req) {
        if(CollectionUtil.isEmpty(req)){
            return;
        }
        List<String> keys = req.stream().map(this::getKey).collect(Collectors.toList());
        RedisUtils.del(keys);
    }
}
