package com.bbu.attendance.qr.service.impl;

import com.bbu.attendance.common.exception.QrAlreadyUsedException;
import com.bbu.attendance.common.exception.QrExpiredException;
import com.bbu.attendance.qr.repository.RedisNonceRepository;
import com.bbu.attendance.qr.service.QrNonceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation សម្រាប់ QrNonceService គ្រប់គ្រង Nonce TTL 15s និង Atomic Invalidation
 * បង្កើតគ្រោងទុកជាមុនដោយ Team Leader: ខី វណ្ណារ៉ុង
 * ទទួលខុសត្រូវបំពេញកូដដោយ៖ និស្សិត ស្រិល ម៉េងទី (ថ្ងៃពុធ)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QrNonceServiceImpl implements QrNonceService {

    private final RedisNonceRepository redisNonceRepository;

    @Override
    public void registerNonce(String nonce, Long sessionId, long ttlSeconds) {
        redisNonceRepository.saveNonce(nonce, sessionId, ttlSeconds);
    }

    @Override
    public void validateAndConsumeNonce(String nonce) {
        if (!redisNonceRepository.hasNonce(nonce)) {
            log.warn("Nonce {} not found in Redis or already expired/consumed", nonce);
            throw new QrExpiredException("QR Code has expired or is invalid. Please scan the current live code.");
        }
        boolean consumed = redisNonceRepository.consumeNonce(nonce);
        if (!consumed) {
            log.warn("Nonce {} already burned during concurrent scan", nonce);
            throw new QrAlreadyUsedException("This QR Code has already been scanned. Replay attack blocked!");
        }
    }
}
