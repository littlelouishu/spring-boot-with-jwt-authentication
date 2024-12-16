package com.example.springbootwithjwtauthentication.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_ID_MDC_KEY = "REQUEST_ID";
    private static final String USER_ID_MDC_KEY = "USER_ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Get or generate request ID
            String requestId = request.getHeader(REQUEST_ID_HEADER);
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }
            
            // Add request ID to MDC
            MDC.put(REQUEST_ID_MDC_KEY, requestId);
            
            // Add request ID to response header
            response.addHeader(REQUEST_ID_HEADER, requestId);
            
            // Set anonymous for user ID if not authenticated
            MDC.put(USER_ID_MDC_KEY, "anonymous");
            
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
