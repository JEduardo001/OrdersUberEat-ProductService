package com.SoftwareOrdersUberEats.productService.security.filters;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static com.SoftwareOrdersUberEats.productService.constant.TracerConstants.CORRELATION_HEADER;
import static com.SoftwareOrdersUberEats.productService.constant.TracerConstants.CORRELATION_KEY;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String id = request.getHeader(CORRELATION_HEADER);
        System.out.println("id correl " + id);

        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }

        try {
            MDC.put(CORRELATION_KEY, id);
            response.setHeader(CORRELATION_HEADER, id);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_KEY);
        }
    }
}
