package com.bbu.attendance.common.handler;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller Advice សម្រាប់ចាប់ Exception ជាក់លាក់របស់ QR Code (Invalid, Expired, AlreadyUsed)
 * បង្កើតគ្រោងទុកជាមុនដោយ Team Leader: ខី វណ្ណារ៉ុង
 * ទទួលខុសត្រូវបំពេញកូដដោយ៖ និស្សិត សេង ស៊ីមផា (ថ្ងៃសុក្រ)
 */
@RestControllerAdvice
public class QrExceptionHandler {
    // TODO: និស្សិត សេង ស៊ីមផា ត្រូវបំពេញ @ExceptionHandler Methods នៅទីនេះ
}
