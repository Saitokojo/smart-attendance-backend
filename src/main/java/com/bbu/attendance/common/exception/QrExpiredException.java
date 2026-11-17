package com.bbu.attendance.common.exception;

/**
 * Exception សម្រាប់ចាប់កំហុសនៅពេល QR Code ផុតកំណត់ ១៥ វិនាទី (Expired Token)
 * បង្កើតគ្រោងទុកជាមុនដោយ Team Leader: ខី វណ្ណារ៉ុង
 * ទទួលខុសត្រូវបំពេញកូដដោយ៖ និស្សិត ស្រិល ម៉េងទី (ថ្ងៃពុធ)
 */
public class QrExpiredException extends RuntimeException {
    public QrExpiredException(String message) {
        super(message);
    }
}
