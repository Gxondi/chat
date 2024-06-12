package com.hyh.mallchat.common.user.service.cache;

import com.hyh.mallchat.common.common.constant.RedisKey;
import com.hyh.mallchat.common.common.domain.enums.ItemTypeEnum;
import com.hyh.mallchat.common.common.service.cache.AbstractRedisStringCache;
import com.hyh.mallchat.common.user.dao.UserBackpackDao;
import com.hyh.mallchat.common.user.domain.dto.SummaryInfoDTO;
import com.hyh.mallchat.common.user.domain.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UserSummaryCache extends AbstractRedisStringCache<Long,SummaryInfoDTO> {
    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private ItemCache itemCache;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Override
    protected Long getExpireSeconds() {
        return 5*60L;
    }

    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_SUMMARY_STRING, uid);
    }

    @Override
    protected Map<Long,SummaryInfoDTO> load(List<Long> uidList) {
        if (uidList.size() == 0)
            return null;
        Map<Long, User> userMap = userInfoCache.getBatch(uidList);
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        List<Long> itemConfigIds = itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList());
        List<UserBackpack> userBackpackList = userBackpackDao.getByItemIds(uidList, itemConfigIds);
        // 按照uid,把用户背包信息分组
        Map<Long, List<UserBackpack>> userBadgeMap = userBackpackList.stream().collect(Collectors.groupingBy(UserBackpack::getUid));
        return uidList.stream().map(uid->{
            SummaryInfoDTO summaryInfoDTO = new SummaryInfoDTO();
            User user = userMap.get(uid);
            if(Objects.isNull(user)){
                return null;
            }
            List<UserBackpack> userBackpacks = userBadgeMap.getOrDefault(user.getId(), null);
            summaryInfoDTO.setUid(user.getId());
            summaryInfoDTO.setName(user.getName());
            summaryInfoDTO.setAvatar(user.getAvatar());
            summaryInfoDTO.setLocPlace(Optional
                    .ofNullable(user.getIpInfo())
                    .map(IpInfo::getUpdateIpdetail)
                    .map(IpDetail::getCity)
                    .orElse(null));
            summaryInfoDTO.setWearingItemId(userBackpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toList()));
            return summaryInfoDTO;
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SummaryInfoDTO::getUid, Function.identity()));
    }
}
