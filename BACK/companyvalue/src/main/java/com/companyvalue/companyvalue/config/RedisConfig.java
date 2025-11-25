package com.companyvalue.companyvalue.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
        // 1. ObjectMapper 커스터마이징 (날짜 타입 지원 추가)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDate 처리를 위한 모듈 등록
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜를 배열[2024,1,1] 대신 문자열 "2024-01-01"로 저장

        // 데이터를 다시 객체로 변환할 때 클래스 정보를 포함하도록 설정 (GenericJackson2JsonRedisSerializer 필수 설정)
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        // 2. 커스텀 ObjectMapper를 사용하는 Serializer 생성
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // 3. Redis 설정에 적용
        // 기본 설정: 모든 캐시의 만료 시간을 1시간으로 설정
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // 1시간
                .disableCachingNullValues() // null 값은 캐싱하지않음
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

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
