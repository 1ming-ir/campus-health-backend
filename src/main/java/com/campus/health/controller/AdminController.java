package com.campus.health.controller;

import com.campus.health.common.ApiResponse;
import com.campus.health.entity.Doctor;
import com.campus.health.entity.HealthArticle;
import com.campus.health.entity.User;
import com.campus.health.mapper.DoctorMapper;
import com.campus.health.mapper.HealthArticleMapper;
import com.campus.health.mapper.UserMapper;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserMapper userMapper;
    private final DoctorMapper doctorMapper;
    private final HealthArticleMapper articleMapper;

    public AdminController(UserMapper userMapper, DoctorMapper doctorMapper, HealthArticleMapper articleMapper) {
        this.userMapper = userMapper;
        this.doctorMapper = doctorMapper;
        this.articleMapper = articleMapper;
    }

    @GetMapping("/users")
    public ApiResponse<List<User>> users() {
        List<User> users = userMapper.findAll();
        users.forEach(user -> user.setPassword(null));
        return ApiResponse.ok(users);
    }

    @PutMapping("/users/{id}/status")
    public ApiResponse<String> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        userMapper.updateStatus(id, body.getOrDefault("status", "ENABLED"));
        return ApiResponse.ok("用户状态已更新");
    }

    @GetMapping("/doctors")
    public ApiResponse<List<Doctor>> doctors() {
        return ApiResponse.ok(doctorMapper.findAll());
    }

    @PostMapping("/doctors")
    public ApiResponse<Doctor> createDoctor(@RequestBody Doctor doctor) {
        doctorMapper.insert(doctor);
        return ApiResponse.ok(doctor);
    }

    @PutMapping("/doctors/{id}")
    public ApiResponse<String> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        doctor.setId(id);
        doctorMapper.update(doctor);
        return ApiResponse.ok("医生信息已更新");
    }

    @GetMapping("/articles")
    public ApiResponse<List<HealthArticle>> articles() {
        return ApiResponse.ok(articleMapper.findAll());
    }

    @PostMapping("/articles")
    public ApiResponse<HealthArticle> createArticle(@RequestBody HealthArticle article) {
        if (article.getStatus() == null) {
            article.setStatus("PUBLISHED");
        }
        articleMapper.insert(article);
        return ApiResponse.ok(article);
    }

    @PutMapping("/articles/{id}")
    public ApiResponse<String> updateArticle(@PathVariable Long id, @RequestBody HealthArticle article) {
        article.setId(id);
        if (article.getStatus() == null) {
            article.setStatus("PUBLISHED");
        }
        articleMapper.update(article);
        return ApiResponse.ok("科普文章已更新");
    }

    @PutMapping("/articles/{id}/status")
    public ApiResponse<String> updateArticleStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        articleMapper.updateStatus(id, body.getOrDefault("status", "OFFLINE"));
        return ApiResponse.ok("科普文章状态已更新");
    }
}
