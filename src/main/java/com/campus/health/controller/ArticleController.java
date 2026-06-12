package com.campus.health.controller;

import com.campus.health.common.ApiResponse;
import com.campus.health.entity.Announcement;
import com.campus.health.entity.Doctor;
import com.campus.health.entity.HealthArticle;
import com.campus.health.entity.Medicine;
import com.campus.health.mapper.AnnouncementMapper;
import com.campus.health.mapper.DoctorMapper;
import com.campus.health.mapper.HealthArticleMapper;
import com.campus.health.mapper.MedicineMapper;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ArticleController {
    private final HealthArticleMapper articleMapper;
    private final DoctorMapper doctorMapper;
    private final MedicineMapper medicineMapper;
    private final AnnouncementMapper announcementMapper;

    public ArticleController(HealthArticleMapper articleMapper, DoctorMapper doctorMapper,
                             MedicineMapper medicineMapper, AnnouncementMapper announcementMapper) {
        this.articleMapper = articleMapper;
        this.doctorMapper = doctorMapper;
        this.medicineMapper = medicineMapper;
        this.announcementMapper = announcementMapper;
    }

    @GetMapping("/articles")
    public ApiResponse<List<HealthArticle>> articles() {
        return ApiResponse.ok(articleMapper.findPublished());
    }

    @GetMapping("/doctors")
    public ApiResponse<List<Doctor>> doctors() {
        return ApiResponse.ok(doctorMapper.findAll());
    }

    @GetMapping("/medicines")
    public ApiResponse<List<Medicine>> medicines() {
        return ApiResponse.ok(medicineMapper.findPublished());
    }

    @GetMapping("/announcements")
    public ApiResponse<List<Announcement>> announcements() {
        return ApiResponse.ok(announcementMapper.findPublished());
    }
}