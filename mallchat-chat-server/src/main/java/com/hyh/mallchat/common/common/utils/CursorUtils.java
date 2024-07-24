package com.hyh.mallchat.common.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyh.mallchat.common.common.domain.vo.req.CursorPageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.hyh.mallchat.common.user.domain.entity.UserFriend;
import org.springframework.data.redis.core.ZSetOperations;


import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CursorUtils {
    /**
     * 分页查询
     *
     * @param mapper       mapper参数---那个dao层调用的
     * @param request
     * @param initWrapper  查询条件  ---外面传进来的
     * @param cursorColumn 游标字段
     * @param <T>
     * @return
     */
    public static <T> CursorPageBaseResp<T> getCursorPageByMysql(IService<T> mapper, CursorPageBaseReq request, Consumer<LambdaQueryWrapper<T>> initWrapper, SFunction<T, ?> cursorColumn) {
        //游标字段类型
        //根据Lambda表达式获取返回类型
        // System.out.println(LambdaUtils.getReturnType(UserFriend::getId));
        // class java.lang.Long
        Class<?> cursorType = LambdaUtils.getReturnType(cursorColumn);
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        //额外条件
        initWrapper.accept(wrapper);

        if (StrUtil.isNotBlank(request.getCursor())) {
            //类型一致cursorColumn ， parseCursor(request.getCursor(), cursorType)转换方法
            wrapper.lt(cursorColumn, parseCursor(request.getCursor(), cursorType));
        }

        wrapper.orderByDesc(cursorColumn);
        //分页
        Page<T> page = mapper.page(request.plusPage(), wrapper);
        //取出游标
        String cursor = Optional.ofNullable(CollectionUtil.getLast(page.getRecords()))
                .map(cursorColumn)
                .map(CursorUtils::toCursor)
                .orElse(null);
        //判断是否最后一页
        Boolean isLast = page.getRecords().size() != request.getPageSize();
        return new CursorPageBaseResp<>(cursor, isLast, page.getRecords());
    }

    public static <T> CursorPageBaseResp<Pair<T, Double>> getCursorPageByRedis(CursorPageBaseReq cursorPageBaseReq, String redisKey, Function<String, T> typeConvert) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples;
        if (StrUtil.isBlank(cursorPageBaseReq.getCursor())) {
            //为空，第一次调用
            typedTuples = RedisUtils.zReverseRangeWithScores(redisKey, cursorPageBaseReq.getPageSize());
        } else {
            typedTuples = RedisUtils.zReverseRangeByScoreWithScores(redisKey, Double.parseDouble(cursorPageBaseReq.getCursor()), cursorPageBaseReq.getPageSize());
        }
        List<Pair<T, Double>> result = typedTuples.stream()
                .map(t -> Pair.of(typeConvert.apply(t.getValue()), t.getScore()))
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .collect(Collectors.toList());
        //获取最后一个值
        String cursor = Optional.ofNullable(CollectionUtil.getLast(result))
                .map(Pair::getValue)
                .map(String::valueOf)
                .orElse(null);

        boolean isLast = result.size() != cursorPageBaseReq.getPageSize();
        return new CursorPageBaseResp<>(cursor,isLast,result);
    }

    /**
     * parseCursor 返回 Long 或其他类型： 这个方法将字符串游标转换成其原始的数据类型（如日期的时间戳），这样数据库查询可以利用这个类型进行比较操作，如比较日期或数字等。
     * toCursor 返回 String： 无论原始数据类型是什么，这个方法将游标的值统一转换成字符串，因为字符串形式的游标易于通过URL传输，且可以被不同的系统或语言处理。
     */
    private static String toCursor(Object o) {
        if (o instanceof Date) {
            return String.valueOf(((Date) o).getTime());
        } else {
            return o.toString();
        }
    }

    /**
     * 这个方法根据数据的类型（在这个例子中是检查是否为日期类型）来决定数据如何被解析和返回。如果是日期类型，需要将字符串转换成日期对象；如果不是，就直接返回原始字符串。这样的设计使得数据处理更加灵活和类型安全。
     *
     * @param cursor
     * @param cursorClass
     * @return
     */
    private static Object parseCursor(String cursor, Class<?> cursorClass) {
        //根据游标字段类型转换
        if (Date.class.isAssignableFrom(cursorClass)) {
            return new Date(Long.parseLong(cursor));
        } else {
            return cursor;
        }
    }


}
