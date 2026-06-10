package com.campus.health.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        String requiredRole = requiredRole(path, request.getMethod());
        if (requiredRole == null) {
            return true;
        }
        String role = roleFromToken(request.getHeader("Authorization"));
        if (requiredRole.equals(role)) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Map.of(
            "code", 403,
            "message", "当前角色无权访问该接口",
            "data", null
        )));
        return false;
    }

    private String requiredRole(String path, String method) {
        if (path.startsWith("/api/admin/")) {
            return "ADMIN";
        }
        if (path.equals("/api/consultations") && "GET".equalsIgnoreCase(method)) {
            return "DOCTOR";
        }
        if (path.matches("/api/consultations/\\d+/reply")) {
            return "DOCTOR";
        }
        if (path.equals("/api/appointments/doctor") || path.matches("/api/appointments/\\d+/status")) {
            return "DOCTOR";
        }
        if (path.equals("/api/consultations") && "POST".equalsIgnoreCase(method)) {
            return "STUDENT";
        }
        if (path.equals("/api/consultations/my")) {
            return "STUDENT";
        }
        if (path.equals("/api/appointments") && "POST".equalsIgnoreCase(method)) {
            return "STUDENT";
        }
        if (path.equals("/api/appointments/my")) {
            return "STUDENT";
        }
        return null;
    }

    private String roleFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer demo-token-")) {
            return null;
        }
        String suffix = authorization.substring("Bearer demo-token-".length()).toUpperCase();
        if ("STUDENT".equals(suffix) || "DOCTOR".equals(suffix) || "ADMIN".equals(suffix)) {
            return suffix;
        }
        return null;
    }
}
