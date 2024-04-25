package com.hyh.mallchat.common.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    @Bean("caffeineCacheManager") // Bean的名称
    @Primary // 优先使用
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 方案一(常用)：定制化缓存Cache
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES) // 设置写缓存后5分钟过期
                .initialCapacity(100) // 设置缓存容器的初始容量为100
                .maximumSize(200)); // 设置缓存最大容量为200
        return cacheManager; // 返回CacheManager对象
    }

}
