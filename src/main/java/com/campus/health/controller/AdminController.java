package com.campus.health.controller;

import com.campus.health.common.ApiResponse;
import com.campus.health.entity.Announcement;
import com.campus.health.entity.Doctor;
import com.campus.health.entity.HealthArticle;
import com.campus.health.entity.Medicine;
import com.campus.health.entity.User;
import com.campus.health.mapper.AnnouncementMapper;
import com.campus.health.mapper.AppointmentMapper;
import com.campus.health.mapper.ConsultationRecordMapper;
import com.campus.health.mapper.DoctorMapper;
import com.campus.health.mapper.HealthArticleMapper;
import com.campus.health.mapper.MedicineMapper;
import com.campus.health.mapper.UserMapper;
import java.util.HashMap;
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
    private final AppointmentMapper appointmentMapper;
    private final ConsultationRecordMapper consultationMapper;
    private final MedicineMapper medicineMapper;
    private final AnnouncementMapper announcementMapper;

    public AdminController(UserMapper userMapper, DoctorMapper doctorMapper, HealthArticleMapper articleMapper,
                           AppointmentMapper appointmentMapper, ConsultationRecordMapper consultationMapper,
                           MedicineMapper medicineMapper, AnnouncementMapper announcementMapper) {
        this.userMapper = userMapper;
        this.doctorMapper = doctorMapper;
        this.articleMapper = articleMapper;
        this.appointmentMapper = appointmentMapper;
        this.consultationMapper = consultationMapper;
        this.medicineMapper = medicineMapper;
        this.announcementMapper = announcementMapper;
    }

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        Map<String, Object> data = new HashMap<>();
        data.put("users", userMapper.findAll().size());
        data.put("doctors", doctorMapper.findAll().size());
        data.put("articles", articleMapper.findAll().size());
        data.put("appointments", appointmentMapper.findAll().size());
        data.put("consultations", consultationMapper.findAllWithStudent().size());
        data.put("medicines", medicineMapper.findAll().size());
        data.put("announcements", announcementMapper.findAll().size());
        return ApiResponse.ok(data);
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
        return ApiResponse.ok("鐢ㄦ埛鐘舵€佸凡鏇存柊");
    }

    @GetMapping("/doctors")
    public ApiResponse<List<Doctor>> doctors() {
        return ApiResponse.ok(doctorMapper.findAll());
    }

    @PostMapping("/doctors")
    public ApiResponse<Doctor> createDoctor(@RequestBody Doctor doctor) {
        if (doctor.getStatus() == null) {
            doctor.setStatus("ENABLED");
        }
        doctorMapper.insert(doctor);
        return ApiResponse.ok(doctor);
    }

    @PutMapping("/doctors/{id}")
    public ApiResponse<String> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        doctor.setId(id);
        doctorMapper.update(doctor);
        return ApiResponse.ok("鍖荤敓璧勬枡宸叉洿鏂?);
    }

    @GetMapping("/articles")
    public ApiResponse<List<HealthArticle>> articles() {
        return ApiResponse.ok(articleMapper.findAll());
    }

    @PutMapping("/articles/{id}/status")
    public ApiResponse<String> updateArticleStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        articleMapper.updateStatus(id, body.getOrDefault("status", "PUBLISHED"));
        return ApiResponse.ok("绉戞櫘鏂囩珷鐘舵€佸凡鏇存柊");
    }

    @GetMapping("/appointments")
    public ApiResponse<List<Map<String, Object>>> appointments() {
        return ApiResponse.ok(appointmentMapper.findAll());
    }

    @GetMapping("/consultations")
    public ApiResponse<List<Map<String, Object>>> consultations() {
        return ApiResponse.ok(consultationMapper.findAllWithStudent());
    }

    @GetMapping("/medicines")
    public ApiResponse<List<Medicine>> medicines() {
        return ApiResponse.ok(medicineMapper.findAll());
    }

    @PostMapping("/medicines")
    public ApiResponse<Medicine> createMedicine(@RequestBody Medicine medicine) {
        if (medicine.getStatus() == null) {
            medicine.setStatus("PUBLISHED");
        }
        medicineMapper.insert(medicine);
        return ApiResponse.ok(medicine);
    }

    @PutMapping("/medicines/{id}")
    public ApiResponse<String> updateMedicine(@PathVariable Long id, @RequestBody Medicine medicine) {
        medicine.setId(id);
        medicineMapper.update(medicine);
        return ApiResponse.ok("鑽搧绉戞櫘宸叉洿鏂?);
    }

    @GetMapping("/announcements")
    public ApiResponse<List<Announcement>> announcements() {
        return ApiResponse.ok(announcementMapper.findAll());
    }

    @PostMapping("/announcements")
    public ApiResponse<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        if (announcement.getStatus() == null) {
            announcement.setStatus("PUBLISHED");
        }
        announcementMapper.insert(announcement);
        return ApiResponse.ok(announcement);
    }

    @PutMapping("/announcements/{id}")
    public ApiResponse<String> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        announcementMapper.update(announcement);
        return ApiResponse.ok("鍏憡宸叉洿鏂?);
    }
}