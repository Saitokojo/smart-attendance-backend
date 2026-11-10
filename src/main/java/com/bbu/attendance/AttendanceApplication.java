package com.bbu.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AttendanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceApplication.class, args);
        System.out.println("=================================================");
        System.out.println(">>> SMART ATTENDANCE BACKEND STARTED ON PORT 8080");
        System.out.println(">>> Java 21 Virtual Threads & HikariCP: ACTIVE");
        System.out.println(">>> Endpoints: /api/auth/, /api/admin/audit-logs, /api/admin/system/metrics");
        System.out.println("=================================================");
    }
}
