package com.bbu.attendance.devops.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class PerformanceLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            response.setHeader("X-Response-Time-Millis", String.valueOf(duration));

            if (duration > 500) {
                log.warn("⚠️ [SLOW REQUEST] {} {} took {}ms (>500ms threshold)", method, uri, duration);
            } else {
                log.info("⚡ [HTTP METRIC] {} {} executed in {}ms (Status: {})", method, uri, duration, response.getStatus());
            }
        }
    }
}
