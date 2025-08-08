package com.todo.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.*;
import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
        var t = new org.springframework.data.redis.core.RedisTemplate<String, Object>();
        t.setConnectionFactory(cf);
        t.setKeySerializer(new StringRedisSerializer());
        t.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        t.setHashKeySerializer(new StringRedisSerializer());
        t.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return t;
    }

    // 캐시 매니저(JSON 직렬화 + TTL)
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration cfg = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // yml의 spring.cache.redis.time-to-live와 동일하게
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(cf)
                .cacheDefaults(cfg)
                .build();
    }

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        Logger log = LoggerFactory.getLogger("CacheErrorHandler");
        return new SimpleCacheErrorHandler() {
            @Override public void handleCacheGetError(RuntimeException ex, org.springframework.cache.Cache cache, Object key) {
                log.warn("Redis GET 실패 - cache={}, key={}, err={}", cache.getName(), key, ex.toString());
            }
            @Override public void handleCachePutError(RuntimeException ex, org.springframework.cache.Cache cache, Object key, Object value) {
                log.warn("Redis PUT 실패 - cache={}, key={}, err={}", cache.getName(), key, ex.toString());
            }
            @Override public void handleCacheEvictError(RuntimeException ex, org.springframework.cache.Cache cache, Object key) {
                log.warn("Redis EVICT 실패 - cache={}, key={}, err={}", cache.getName(), key, ex.toString());
            }
            @Override public void handleCacheClearError(RuntimeException ex, org.springframework.cache.Cache cache) {
                log.warn("Redis CLEAR 실패 - cache={}, err={}", cache.getName(), ex.toString());
            }
        };
    }
}
