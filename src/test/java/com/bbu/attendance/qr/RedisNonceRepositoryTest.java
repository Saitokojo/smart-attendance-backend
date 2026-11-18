package com.bbu.attendance.qr;

import com.bbu.attendance.qr.repository.RedisNonceRepository;
import com.bbu.attendance.qr.repository.impl.RedisNonceRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for RedisNonceRepositoryImpl (Low-Level Redis Ops)")
class RedisNonceRepositoryTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisNonceRepository redisNonceRepository;

    @BeforeEach
    void setUp() {
        redisNonceRepository = new RedisNonceRepositoryImpl(stringRedisTemplate);
    }

    @Test
    @DisplayName("1. saveNonce should set key with prefix 'qr:nonce:' and TTL duration")
    void testSaveNonce_shouldCallRedisSet() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        String nonce = "sample-nonce-123";
        Long sessionId = 99L;
        long ttlSeconds = 15L;

        redisNonceRepository.saveNonce(nonce, sessionId, ttlSeconds);

        verify(valueOperations, times(1)).set("qr:nonce:" + nonce, "99", Duration.ofSeconds(15));
    }

    @Test
    @DisplayName("2. hasNonce should check key existence with prefix 'qr:nonce:'")
    void testHasNonce_shouldReturnTrueWhenKeyExists() {
        String nonce = "active-nonce-456";
        when(stringRedisTemplate.hasKey("qr:nonce:" + nonce)).thenReturn(Boolean.TRUE);

        boolean exists = redisNonceRepository.hasNonce(nonce);

        assertTrue(exists);
        verify(stringRedisTemplate, times(1)).hasKey("qr:nonce:" + nonce);
    }

    @Test
    @DisplayName("3. hasNonce should return false when key does not exist or null")
    void testHasNonce_shouldReturnFalseWhenKeyDoesNotExist() {
        String nonce = "missing-nonce";
        when(stringRedisTemplate.hasKey("qr:nonce:" + nonce)).thenReturn(Boolean.FALSE);

        boolean exists = redisNonceRepository.hasNonce(nonce);

        assertFalse(exists);
    }

    @Test
    @DisplayName("4. consumeNonce should delete key atomically and return true if deleted")
    void testConsumeNonce_shouldReturnTrueWhenDeleted() {
        String nonce = "burn-nonce-789";
        when(stringRedisTemplate.delete("qr:nonce:" + nonce)).thenReturn(Boolean.TRUE);

        boolean burned = redisNonceRepository.consumeNonce(nonce);

        assertTrue(burned);
        verify(stringRedisTemplate, times(1)).delete("qr:nonce:" + nonce);
    }

    @Test
    @DisplayName("5. consumeNonce should return false when delete returns false or null")
    void testConsumeNonce_shouldReturnFalseWhenAlreadyDeleted() {
        String nonce = "already-burned-nonce";
        when(stringRedisTemplate.delete("qr:nonce:" + nonce)).thenReturn(Boolean.FALSE);

        boolean burned = redisNonceRepository.consumeNonce(nonce);

        assertFalse(burned);
        verify(stringRedisTemplate, times(1)).delete("qr:nonce:" + nonce);
    }
}
