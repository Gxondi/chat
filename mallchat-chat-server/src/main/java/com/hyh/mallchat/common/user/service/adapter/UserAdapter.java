package com.hyh.mallchat.common.user.service.adapter;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.base.Objects;
import com.hyh.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.hyh.mallchat.common.user.domain.entity.ItemConfig;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.domain.entity.UserBackpack;
import com.hyh.mallchat.common.user.domain.vo.resp.BadgesResp;
import com.hyh.mallchat.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.beans.BeanUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserAdapter {
    public static User buildUserSave(String openId) {
        User build = User.builder().openId(openId).build();
        return build;
    }
    public static User buildAuthorizeUser(WxOAuth2UserInfo userInfo) {
       return User.builder().name(userInfo.getNickname()).avatar(userInfo.getHeadImgUrl()).build();
    }

    public static UserInfoResp buildUserInfoResp(User user, Integer countByValidItemId) {
        UserInfoResp userInfoResp = new UserInfoResp();
        BeanUtils.copyProperties(user, userInfoResp);
        userInfoResp.setUserId(user.getId());
        userInfoResp.setModifyNameTimes(countByValidItemId);
        return userInfoResp;
    }

    /**
     * 查询用户勋章列表
     * @param allBadges
     * @param backpacks
     * @param user
     * @return 返回List<BadgesResp> 组装好的数据
     */
    public static List<BadgesResp> buildBadgesResp(List<ItemConfig> allBadges, List<UserBackpack> backpacks, User user) {
        //返回前端数据，所有徽章，用户拥有的徽章

        //用户拥有的徽章id
        Set<Long> obtainBadges = backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return allBadges.stream().map(itemConfig -> {
            BadgesResp badgesResp = new BadgesResp();
            BeanUtils.copyProperties(itemConfig, badgesResp);
            //用户拥有的徽章
            badgesResp.setObtain(obtainBadges.contains(itemConfig.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            //用户配带的勋章
            badgesResp.setWearing(Objects.equal(itemConfig.getId(), user.getItemId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            return badgesResp;
            //进行排序
        }).sorted(Comparator.comparing(BadgesResp::getWearing, Comparator.reverseOrder()).thenComparing(BadgesResp::getObtain,Comparator.reverseOrder())).collect(Collectors.toList());
    }
}
