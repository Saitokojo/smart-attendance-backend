package com.bbu.attendance.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Exception សម្រាប់ចាប់កំហុសនៅពេល QR Code ត្រូវបានស្កេនរួចម្តង (Anti-Replay Attack)
 * បង្កើតគ្រោងទុកជាមុនដោយ Team Leader: ខី វណ្ណារ៉ុង
 * ទទួលខុសត្រូវបំពេញកូដដោយ៖ និស្សិត ស្រិល ម៉េងទី (ថ្ងៃពុធ)
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class QrAlreadyUsedException extends RuntimeException {
    public QrAlreadyUsedException(String message) {
        super(message);
    }
}
