package com.bbu.attendance.qr.repository.impl;

import com.bbu.attendance.qr.repository.RedisNonceRepository;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/**
 * Implementation សម្រាប់ RedisNonceRepository ដោយប្រើ StringRedisTemplate
 * បង្កើតគ្រោងទុកជាមុនដោយ Team Leader: ខី វណ្ណារ៉ុង
 * ទទួលខុសត្រូវបំពេញកូដដោយ៖ និស្សិត ស្រិល ម៉េងទី (ថ្ងៃពុធ)
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisNonceRepositoryImpl implements RedisNonceRepository {

    private static final String NONCE_PREFIX = "qr:nonce:";
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveNonce(String nonce, Long sessionId, long ttlSeconds) {
        String key = NONCE_PREFIX + nonce;
        stringRedisTemplate.opsForValue().set(key, String.valueOf(sessionId), Duration.ofSeconds(ttlSeconds));
        log.info("Saved Redis Nonce: {} with TTL {}s for sessionId {}", nonce, ttlSeconds, sessionId);
    }

    @Override
    public boolean hasNonce(String nonce) {
        String key = NONCE_PREFIX + nonce;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    @Override
    public boolean consumeNonce(String nonce) {
        String key = NONCE_PREFIX + nonce;
        Boolean deleted = stringRedisTemplate.delete(key);
        log.info("Consumed Nonce: {} (Result: {})", nonce, deleted);
        return Boolean.TRUE.equals(deleted);
    }
}
