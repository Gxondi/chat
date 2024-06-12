package com.hyh.mallchat.common.user.service.cache;

import com.hyh.mallchat.common.user.dao.ItemConfigDao;
import com.hyh.mallchat.common.user.domain.entity.ItemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemCache {
    @Autowired
    private ItemConfigDao itemConfigDao;
    @Cacheable(cacheNames = "item", key = "'itemByType:'+#type") // 获取缓存
    public List<ItemConfig> getByType(Integer type) {
        return itemConfigDao.getByType(type);
    }
    @CacheEvict(cacheNames = "item", key = "'itemByType:'+#type") // 清除缓存
    public void clearByType(Integer type) {
    }
    @Cacheable(cacheNames = "item", key = "'itemByi=ItemId:'+#itemId") // 获取缓存
    public ItemConfig getByItemId(Long itemId) {
        return itemConfigDao.getById(itemId);
    }
}
