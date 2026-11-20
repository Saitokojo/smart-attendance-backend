package com.bbu.attendance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@SpringBootApplication
@EnableAsync
public class AttendanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceApplication.class, args);
        log.info("=================================================");
        log.info(">>> SMART ATTENDANCE BACKEND STARTED ON PORT 8080");
        log.info(">>> Java 21 Virtual Threads & HikariCP: ACTIVE");
        log.info(">>> Endpoints: /api/auth/, /api/admin/audit-logs, /api/admin/system/metrics");
        log.info("=================================================");
    }
}
