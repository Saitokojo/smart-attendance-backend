package com.bbu.attendance.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidQrException extends RuntimeException {
    public InvalidQrException(String message) {
        super(message);
    }
}
