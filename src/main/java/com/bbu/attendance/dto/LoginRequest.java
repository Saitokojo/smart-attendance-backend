package com.bbu.attendance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email មិនអាចទទេបានទេ")
    @Email(message = "ទម្រង់ Email មិនត្រឹមត្រូវ")
    private String email;

    @NotBlank(message = "Password មិនអាចទទេបានទេ")
    private String password;
}