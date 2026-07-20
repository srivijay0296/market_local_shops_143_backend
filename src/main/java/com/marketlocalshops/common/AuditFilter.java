package com.marketlocalshops.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class AuditFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, java.io.IOException {
        
        long startTime = System.currentTimeMillis();
        
        // Process request
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            // Extract request details
            String uri = request.getRequestURI();
            String method = request.getMethod();
            String ipAddress = request.getRemoteAddr();
            int status = response.getStatus();
            
            // Extract user security principal
            String username = "anonymous";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                username = auth.getName();
            }
            
            // Log audit message
            log.info("AUDIT - User: [{}], Method: [{}], URI: [{}], Status: [{}], Duration: [{}ms], IP: [{}]",
                    username, method, uri, status, duration, ipAddress);
        }
    }
}
