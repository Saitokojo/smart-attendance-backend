package com.bbu.attendance.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCheckInRequest {

    @NotNull(message = "Session ID មិនអាចទទេបានឡើយ")
    private Long sessionId;

    @NotBlank(message = "Dynamic QR Token មិនអាចទទេបានឡើយ")
    private String qrToken;

    @NotBlank(message = "Device UUID មិនអាចទទេបានឡើយ")
    private String deviceUuid;

    @NotNull(message = "Latitude មិនអាចទទេបានឡើយ")
    private BigDecimal latitude;

    @NotNull(message = "Longitude មិនអាចទទេបានឡើយ")
    private BigDecimal longitude;
}