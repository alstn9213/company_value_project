package com.companyvalue.companyvalue.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 기본 설정: 모든 캐시의 만료 시간을 1시간으로 설정
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // 1시간
                .disableCachingNullValues() // null 값은 캐싱하지않음
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())); // Json 포맷 저장

        // 캐시 이름별 별도 TTL 설정 (Map)
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        // 거시 경제 데이터는 하루에 한 번 바뀌므로 길게(24시간) 설정
        cacheConfigurations.put("macro_latest", defaultConfig.entryTtl(Duration.ofHours(24)));
        cacheConfigurations.put("macro_history", defaultConfig.entryTtl(Duration.ofHours(24)));
        // 기업 점수는 자주 조회되지만 변동이 적으므로 적당히(30분) 설정
        cacheConfigurations.put("company_score", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
