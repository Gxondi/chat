package com.hyh.mallchat.common.user.dao;

import com.hyh.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.hyh.mallchat.common.common.domain.vo.req.CursorPageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.hyh.mallchat.common.common.utils.CursorUtils;
import com.hyh.mallchat.common.user.domain.entity.UserFriend;
import com.hyh.mallchat.common.user.mapper.UserFriendMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户联系人表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-11
 */
@Service
public class UserFriendDao extends ServiceImpl<UserFriendMapper, UserFriend> {

    public CursorPageBaseResp<UserFriend> getFriendPage(Long uid, CursorPageBaseReq request) {
//        LambdaQueryChainWrapper<UserFriend> wrapper = lambdaQuery();
//        //游标定位（Cursor是上次的查询到的id位置）找比游标小的值，找之前的数据
//        wrapper.lt(UserFriend::getId, request.getCursor())
//                //查找方向
//                .orderByDesc(UserFriend::getId)
//                //额外条件
//                .eq(UserFriend::getUid, uid);
//        //分页
//        Page<UserFriend> page = page(request.plusPage(), wrapper);
//        //计算游标
//        String cursor = Optional.ofNullable(CollectionUtil.getLast(page.getRecords()))
//                .map(UserFriend::getId)
//                .map(String::valueOf)
//                .orElse(null);
//        boolean isLast = page.getRecords().size()!=request.getPageSize();
        //工具类
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> wrapper.eq(UserFriend::getUid, uid), UserFriend::getId);
    }

    public List<UserFriend> getFriendList(Long uid, List<Long> uidList) {
        return lambdaQuery().eq(UserFriend::getUid, uid)
                .in(UserFriend::getFriendUid, uidList)
                .list();

    }

    public UserFriend getFriend(Long uid, Long targetUid) {
        return lambdaQuery().eq(UserFriend::getUid, uid)
                .eq(UserFriend::getFriendUid, targetUid)
                .one();
    }

    public void deleteFriend(Long uid, Long targetUid) {
        lambdaUpdate().eq(UserFriend::getUid, uid)
                .eq(UserFriend::getFriendUid, targetUid)
                .set(UserFriend::getDeleteStatus, YesOrNoEnum.YES)
                .update();
    }
    /**
     * 查询是否是好友
     * @param uid
     * @param targetUid
     * @return
     */
    public List<UserFriend> getUserFriend(Long uid, Long targetUid) {
        return lambdaQuery()
                .eq(UserFriend::getUid, uid)
                .eq(UserFriend::getFriendUid, targetUid)
                .or()
                .eq(UserFriend::getUid, targetUid)
                .eq(UserFriend::getFriendUid, uid)
                .select(UserFriend::getId)
                .list();
    }
}
