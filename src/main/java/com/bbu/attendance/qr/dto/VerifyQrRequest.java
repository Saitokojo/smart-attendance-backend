package com.bbu.attendance.qr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyQrRequest {
    @NotBlank(message = "QR Token មិនអាចទទេបានឡើយ")
    private String qrToken;

    @NotNull(message = "Session ID មិនអាចទទេបានឡើយ")
    private Long sessionId;

    @NotBlank(message = "Device UUID មិនអាចទទេបានឡើយ")
    private String deviceUuid;

    private Double latitude;
    private Double longitude;
}