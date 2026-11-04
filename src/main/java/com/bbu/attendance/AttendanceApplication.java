package com.bbu.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class AttendanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceApplication.class, args);
        System.out.println("=================================================");
        System.out.println(">>> SMART ATTENDANCE BACKEND STARTED ON PORT 8080");
        System.out.println(">>> Swagger/Postman Endpoint: http://localhost:8080/api/auth/");
        System.out.println("=================================================");
    }
}
