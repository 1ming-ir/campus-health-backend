package com.campus.health.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String path = request.getRequestURI();
        String method = request.getMethod();
        String role = roleFromToken(request.getHeader("Authorization"));
        String required = requiredRole(path, method);
        if (required == null || required.equals(role)) return true;
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> body = new HashMap<>();
        body.put("code", 403);
        body.put("message", "当前账号无权访问该功能");
        body.put("data", null);
        response.getWriter().write(objectMapper.writeValueAsString(body));
        return false;
    }

    private String requiredRole(String path, String method) {
        if (path.startsWith("/api/admin")) return "ADMIN";
        if (path.equals("/api/appointments/doctor") || path.matches("/api/appointments/\\d+/status")) return "DOCTOR";
        if (path.equals("/api/consultations") && "GET".equals(method)) return "DOCTOR";
        if (path.matches("/api/consultations/\\d+/reply")) return "DOCTOR";
        if (path.equals("/api/consultations") && "POST".equals(method)) return "STUDENT";
        if (path.equals("/api/consultations/my") || path.matches("/api/consultations/\\d+") || path.matches("/api/consultations/\\d+/archive")) return "STUDENT";
        if (path.equals("/api/appointments") && "POST".equals(method)) return "STUDENT";
        if (path.equals("/api/appointments/my")) return "STUDENT";
        if (path.matches("/api/appointments/\\d+/cancel")) return "STUDENT";
        return null;
    }

    private String roleFromToken(String authorization) {
        if (authorization == null) return null;
        if (authorization.contains("demo-token-student")) return "STUDENT";
        if (authorization.contains("demo-token-doctor")) return "DOCTOR";
        if (authorization.contains("demo-token-admin")) return "ADMIN";
        return null;
    }
}