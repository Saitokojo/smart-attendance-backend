package com.bbu.attendance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "ឈ្មោះមិនអាចទទេបានទេ (Name is required)")
    private String name;

    @NotBlank(message = "Email មិនអាចទទេបានទេ (Email is required)")
    @Email(message = "ទម្រង់ Email មិនត្រឹមត្រូវ (Invalid email format)")
    private String email;

    @NotBlank(message = "Password មិនអាចទទេបានទេ (Password is required)")
    @Size(min = 6, message = "Password ត្រូវមានយ៉ាងតិច ៦ ខ្ទង់ (Password must be at least 6 characters)")
    private String password;

    @NotBlank(message = "Role មិនអាចទទេបានទេ ត្រូវដាក់ STUDENT ឬ TEACHER")
    private String role; // 'STUDENT' or 'TEACHER'

    private String studentCode; // សម្រាប់សិស្ស (ឧ. BBU-STU-001)
    private String phone;
}