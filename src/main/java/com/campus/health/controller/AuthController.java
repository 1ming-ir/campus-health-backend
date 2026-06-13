package com.campus.health.controller;

import com.campus.health.common.ApiResponse;
import com.campus.health.dto.LoginRequest;
import com.campus.health.dto.RegisterRequest;
import com.campus.health.entity.Doctor;
import com.campus.health.entity.User;
import com.campus.health.mapper.DoctorMapper;
import com.campus.health.mapper.UserMapper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserMapper userMapper;
    private final DoctorMapper doctorMapper;

    public AuthController(UserMapper userMapper, DoctorMapper doctorMapper) {
        this.userMapper = userMapper;
        this.doctorMapper = doctorMapper;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest req) {
        User user = userMapper.findByUsername(req.getUsername());
        if (user == null || !user.getPassword().equals(req.getPassword())) {
            return ApiResponse.fail("账号或密码错误");
        }
        if (StringUtils.hasText(req.getRole()) && !req.getRole().equals(user.getRole())) {
            return ApiResponse.fail("账号角色与所选端口不匹配");
        }
        if (!"ENABLED".equals(user.getStatus())) {
            return ApiResponse.fail("该账号已被禁用");
        }
        return ApiResponse.ok(toLoginData(user));
    }

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest req) {
        if (!StringUtils.hasText(req.getUsername()) || !StringUtils.hasText(req.getPassword())) {
            return ApiResponse.fail("请填写学号和密码");
        }
        if (!StringUtils.hasText(req.getRealName())) {
            return ApiResponse.fail("请填写姓名");
        }
        if (!StringUtils.hasText(req.getCollege())) {
            return ApiResponse.fail("请填写学院");
        }
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            return ApiResponse.fail("两次输入的密码不一致");
        }
        if (userMapper.findByUsername(req.getUsername()) != null) {
            return ApiResponse.fail("该账号已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername().trim());
        user.setPassword(req.getPassword());
        user.setRealName(req.getRealName().trim());
        user.setCollege(req.getCollege());
        user.setClassName(req.getClassName());
        user.setPhone(req.getPhone());
        user.setRole("STUDENT");
        user.setStatus("ENABLED");
        userMapper.insert(user);
        return ApiResponse.ok(toLoginData(user));
    }

    @GetMapping("/me")
    public ApiResponse<User> me(@RequestParam(defaultValue = "student") String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) return ApiResponse.fail("用户不存在");
        user.setPassword(null);
        return ApiResponse.ok(user);
    }

    @PutMapping("/profile")
    public ApiResponse<User> profile(@RequestBody User user) {
        if (user.getId() == null) return ApiResponse.fail("缺少用户 ID");
        userMapper.updateProfile(user);
        User updated = userMapper.findById(user.getId());
        updated.setPassword(null);
        return ApiResponse.ok(updated);
    }

    private Map<String, Object> toLoginData(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        data.put("token", "demo-token-" + user.getRole().toLowerCase());
        if ("DOCTOR".equals(user.getRole())) {
            Doctor doctor = doctorMapper.findByUserId(user.getId());
            if (doctor != null) data.put("doctorId", doctor.getId());
        }
        return data;
    }
}