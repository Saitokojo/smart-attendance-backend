package com.bbu.attendance.devops.controller;

import com.bbu.attendance.dto.ApiResponse;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class SystemMetricsController {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemMetrics() {
        Map<String, Object> metrics = new LinkedHashMap<>();

        // 1. JVM & Java 21 Loom Virtual Thread Info
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory() / (1024 * 1024);

        Map<String, Object> jvmMetrics = new LinkedHashMap<>();
        jvmMetrics.put("javaVersion", System.getProperty("java.version"));
        jvmMetrics.put("virtualThreadsSupported", Thread.ofVirtual() != null);
        jvmMetrics.put("availableProcessors", runtime.availableProcessors());
        jvmMetrics.put("heapUsedMb", usedMemory);
        jvmMetrics.put("heapTotalMb", totalMemory);
        jvmMetrics.put("heapMaxMb", maxMemory);
        metrics.put("jvm", jvmMetrics);

        // 2. HikariCP Connection Pool Telemetry
        Map<String, Object> dbPoolMetrics = new LinkedHashMap<>();
        if (dataSource instanceof HikariDataSource hikari) {
            dbPoolMetrics.put("poolName", hikari.getPoolName());
            dbPoolMetrics.put("maximumPoolSize", hikari.getMaximumPoolSize());
            dbPoolMetrics.put("minimumIdle", hikari.getMinimumIdle());
            if (hikari.getHikariPoolMXBean() != null) {
                dbPoolMetrics.put("activeConnections", hikari.getHikariPoolMXBean().getActiveConnections());
                dbPoolMetrics.put("idleConnections", hikari.getHikariPoolMXBean().getIdleConnections());
                dbPoolMetrics.put("totalConnections", hikari.getHikariPoolMXBean().getTotalConnections());
                dbPoolMetrics.put("threadsAwaitingConnection", hikari.getHikariPoolMXBean().getThreadsAwaitingConnection());
            }
        }
        metrics.put("connectionPool", dbPoolMetrics);

        // 3. PostgreSQL Latency Ping
        long startTime = System.currentTimeMillis();
        Integer queryResult = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        long dbLatencyMs = System.currentTimeMillis() - startTime;

        Map<String, Object> dbHealth = new LinkedHashMap<>();
        dbHealth.put("databaseStatus", queryResult != null && queryResult == 1 ? "ONLINE" : "UNHEALTHY");
        dbHealth.put("dbPingLatencyMs", dbLatencyMs);
        metrics.put("database", dbHealth);

        log.info("📊 [SystemMetrics] JVM Used: {}MB | DB Latency: {}ms", usedMemory, dbLatencyMs);

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("ទាញយកទិន្នន័យសុខភាពប្រព័ន្ធ និង Concurrency Telemetry បានជោគជ័យ!")
                .data(metrics)
                .build());
    }
}
