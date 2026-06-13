package com.picko.api.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLogFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Request-ID";
    private static final String MDC_TRACE_ID = "traceId";
    public static final String ATTR_USER_ID = "picko.userId";
    public static final String ATTR_ERROR_CODE = "picko.errorCode";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String traceId = resolveTraceId(request);
        MDC.put(MDC_TRACE_ID, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);

        String method = request.getMethod();
        String uri = buildUri(request);
        String ip = resolveClientIp(request);
        long start = System.currentTimeMillis();

        log.info("→ {}", formatRequest(method, uri, ip));

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            int status = response.getStatus();
            String userId = resolveUserId(request);
            String errorCode = (String) request.getAttribute(ATTR_ERROR_CODE);

            String responseLine = formatResponse(status, duration, userId, errorCode);
            if (status >= 500) {
                log.error("← {}", responseLine);
            } else if (status >= 400) {
                log.warn("← {}", responseLine);
            } else {
                log.info("← {}", responseLine);
            }

            MDC.clear();
        }
    }

    private String resolveTraceId(HttpServletRequest request) {
        String header = request.getHeader(TRACE_ID_HEADER);
        if (header != null && !header.isBlank()) {
            return header;
        }
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private String buildUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        return query != null ? uri + "?" + query : uri;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }
        return maskIp(ip);
    }

    private String maskIp(String ip) {
        if (ip.contains(":")) {
            return ip.substring(0, ip.lastIndexOf(':')) + ":***";
        }
        int lastDot = ip.lastIndexOf('.');
        if (lastDot == -1) return ip;
        return ip.substring(0, lastDot) + ".***";
    }

    private String resolveUserId(HttpServletRequest request) {
        Object userId = request.getAttribute(ATTR_USER_ID);
        return userId != null ? userId.toString() : "anonymous";
    }

    private String formatRequest(String method, String uri, String ip) {
        return String.format("%-6s %s | ip=%s", method, uri, ip);
    }

    private String formatResponse(int status, long duration, String userId, String errorCode) {
        StringBuilder sb = new StringBuilder();
        sb.append(status).append(" | ").append(duration).append("ms | user=").append(userId);
        if (errorCode != null) {
            sb.append(" | ").append(errorCode);
        }
        return sb.toString();
    }
}
