package com.bbu.attendance.common.handler;

import com.bbu.attendance.dto.ApiResponse;
import com.bbu.attendance.common.exception.InvalidQrException;
import com.bbu.attendance.common.exception.QrAlreadyUsedException;
import com.bbu.attendance.common.exception.QrExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller Advice សម្រាប់ចាប់ Exception ជាក់លាក់របស់ Dynamic QR & Attendance Security
 * ទទួលខុសត្រូវដោយ៖ និស្សិត សេង ស៊ីមផា (ថ្ងៃសុក្រ)
 */
@Slf4j
@RestControllerAdvice
public class QrExceptionHandler {

    @ExceptionHandler(InvalidQrException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidQr(InvalidQrException ex) {
        log.warn("Invalid QR detected: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(QrExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleQrExpired(QrExpiredException ex) {
        log.warn("Expired QR detected: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(QrAlreadyUsedException.class)
    public ResponseEntity<ApiResponse<Void>> handleQrAlreadyUsed(QrAlreadyUsedException ex) {
        log.warn("Anti-Replay Attack blocked: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }
}
