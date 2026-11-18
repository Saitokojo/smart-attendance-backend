package com.bbu.attendance.qr;

import com.bbu.attendance.common.exception.QrAlreadyUsedException;
import com.bbu.attendance.common.exception.QrExpiredException;
import com.bbu.attendance.qr.repository.RedisNonceRepository;
import com.bbu.attendance.qr.service.QrNonceService;
import com.bbu.attendance.qr.service.impl.QrNonceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for QrNonceService (Lifecycle, Invalidation & Anti-Replay)")
class QrNonceServiceTest {

    @Mock
    private RedisNonceRepository redisNonceRepository;

    private QrNonceService qrNonceService;

    @BeforeEach
    void setUp() {
        qrNonceService = new QrNonceServiceImpl(redisNonceRepository);
    }

    @Test
    @DisplayName("1. registerNonce should delegate cleanly to Redis repository with TTL 15s")
    void testRegisterNonce_shouldDelegateToRepository() {
        String nonce = UUID.randomUUID().toString();
        Long sessionId = 88L;
        long ttlSeconds = 15L;

        qrNonceService.registerNonce(nonce, sessionId, ttlSeconds);

        verify(redisNonceRepository, times(1)).saveNonce(nonce, sessionId, ttlSeconds);
    }

    @Test
    @DisplayName("2. validateAndConsumeNonce with active valid nonce should consume successfully")
    void testValidateAndConsumeNonce_success() {
        String nonce = UUID.randomUUID().toString();

        when(redisNonceRepository.hasNonce(nonce)).thenReturn(true);
        when(redisNonceRepository.consumeNonce(nonce)).thenReturn(true);

        assertDoesNotThrow(() -> qrNonceService.validateAndConsumeNonce(nonce),
                "Active unconsumed nonce must be validated and consumed without error");

        verify(redisNonceRepository, times(1)).hasNonce(nonce);
        verify(redisNonceRepository, times(1)).consumeNonce(nonce);
    }

    @Test
    @DisplayName("3. validateAndConsumeNonce when nonce does not exist or expired should throw QrExpiredException")
    void testValidateAndConsumeNonce_expired_shouldThrowQrExpiredException() {
        String expiredNonce = UUID.randomUUID().toString();

        when(redisNonceRepository.hasNonce(expiredNonce)).thenReturn(false);

        QrExpiredException exception = assertThrows(QrExpiredException.class, () ->
                qrNonceService.validateAndConsumeNonce(expiredNonce));

        assertTrue(exception.getMessage().contains("expired"), "Exception message should indicate expiration");
        verify(redisNonceRepository, times(1)).hasNonce(expiredNonce);
        verify(redisNonceRepository, never()).consumeNonce(anyString());
    }

    @Test
    @DisplayName("4. validateAndConsumeNonce when nonce was already burned in concurrent race should throw QrAlreadyUsedException")
    void testValidateAndConsumeNonce_replayAttack_shouldThrowQrAlreadyUsedException() {
        String reusedNonce = UUID.randomUUID().toString();

        // Nonce exists when checked, but another thread/student consumes it first (atomic burn returns false)
        when(redisNonceRepository.hasNonce(reusedNonce)).thenReturn(true);
        when(redisNonceRepository.consumeNonce(reusedNonce)).thenReturn(false);

        QrAlreadyUsedException exception = assertThrows(QrAlreadyUsedException.class, () ->
                qrNonceService.validateAndConsumeNonce(reusedNonce));

        assertTrue(exception.getMessage().contains("already been scanned"),
                "Exception message must indicate replay attack blocking");
        verify(redisNonceRepository, times(1)).hasNonce(reusedNonce);
        verify(redisNonceRepository, times(1)).consumeNonce(reusedNonce);
    }
}
