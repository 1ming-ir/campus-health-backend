package com.campus.health.controller;

import com.campus.health.common.ApiResponse;
import com.campus.health.dto.LoginRequest;
import com.campus.health.entity.User;
import com.campus.health.mapper.UserMapper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserMapper userMapper;

    public AuthController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest req) {
        User user = userMapper.findByUsername(req.getUsername());
        if (user == null || !user.getPassword().equals(req.getPassword())) {
            return ApiResponse.fail("账号或密码错误");
        }
        if (req.getRole() != null && !req.getRole().equals(user.getRole())) {
            return ApiResponse.fail("所选角色与账号权限不匹配");
        }
        if (!"ENABLED".equals(user.getStatus())) {
            return ApiResponse.fail("账号已被禁用");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        data.put("token", "demo-token-" + user.getRole().toLowerCase());
        return ApiResponse.ok(data);
    }

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody User user) {
        if (user.getRole() == null) {
            user.setRole("STUDENT");
        }
        if (user.getStatus() == null) {
            user.setStatus("ENABLED");
        }
        userMapper.insert(user);
        user.setPassword(null);
        return ApiResponse.ok(user);
    }

    @GetMapping("/me")
    public ApiResponse<User> me(@RequestParam(defaultValue = "student") String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return ApiResponse.fail("用户不存在");
        }
        user.setPassword(null);
        return ApiResponse.ok(user);
    }
}
