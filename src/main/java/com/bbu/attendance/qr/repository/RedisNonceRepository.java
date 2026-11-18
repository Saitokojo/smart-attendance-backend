package com.bbu.attendance.qr.repository;

/**
 * Interface សម្រាប់គ្រប់គ្រងប្រតិបត្តិការ Nonce លើ In-Memory Redis Database
 * បង្កើតគ្រោងទុកជាមុនដោយ Team Leader: ខី វណ្ណារ៉ុង
 * ទទួលខុសត្រូវបំពេញកូដដោយ៖ និស្សិត ស្រិល ម៉េងទី (ថ្ងៃពុធ)
 */
public interface RedisNonceRepository {
    
    void saveNonce(String nonce, Long sessionId, long ttlSeconds);
    boolean hasNonce(String nonce);
    boolean consumeNonce(String nonce);
}
