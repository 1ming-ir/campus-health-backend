package com.campus.health.controller;

import com.campus.health.common.ApiResponse;
import com.campus.health.dto.LoginRequest;
import com.campus.health.dto.RegisterRequest;
import com.campus.health.entity.User;
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

    public AuthController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest req) {
        User user = userMapper.findByUsername(req.getUsername());
        if (user == null || !user.getPassword().equals(req.getPassword())) {
            return ApiResponse.fail("璐﹀彿鎴栧瘑鐮侀敊璇?);
        }
        if (req.getRole() != null && !req.getRole().equals(user.getRole())) {
            return ApiResponse.fail("鎵€閫夎鑹蹭笌璐﹀彿鏉冮檺涓嶅尮閰?);
        }
        if (!"ENABLED".equals(user.getStatus())) {
            return ApiResponse.fail("璐﹀彿宸茶绂佺敤");
        }
        return ApiResponse.ok(toLoginData(user));
    }

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest req) {
        if (!StringUtils.hasText(req.getUsername()) || !StringUtils.hasText(req.getPassword())) {
            return ApiResponse.fail("瀛﹀彿鍜屽瘑鐮佷笉鑳戒负绌?);
        }
        if (!StringUtils.hasText(req.getRealName())) {
            return ApiResponse.fail("濮撳悕涓嶈兘涓虹┖");
        }
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            return ApiResponse.fail("涓ゆ杈撳叆鐨勫瘑鐮佷笉涓€鑷?);
        }
        if (userMapper.findByUsername(req.getUsername()) != null) {
            return ApiResponse.fail("璇ュ鍙峰凡娉ㄥ唽");
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
        if (user == null) {
            return ApiResponse.fail("鐢ㄦ埛涓嶅瓨鍦?);
        }
        user.setPassword(null);
        return ApiResponse.ok(user);
    }

    @PutMapping("/profile")
    public ApiResponse<User> profile(@RequestBody User user) {
        if (user.getId() == null) {
            return ApiResponse.fail("缂哄皯鐢ㄦ埛 ID");
        }
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
        return data;
    }
}