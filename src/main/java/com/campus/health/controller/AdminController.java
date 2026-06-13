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
import org.springframework.util.StringUtils;
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

    public AdminController(UserMapper userMapper, DoctorMapper doctorMapper, HealthArticleMapper articleMapper, AppointmentMapper appointmentMapper, ConsultationRecordMapper consultationMapper, MedicineMapper medicineMapper, AnnouncementMapper announcementMapper) {
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
        List<User> users = userMapper.findAll();
        List<Map<String, Object>> appointments = appointmentMapper.findAll();
        List<Map<String, Object>> consultations = consultationMapper.findAllWithStudent();
        Map<String, Object> data = new HashMap<>();
        data.put("users", users.size());
        data.put("students", users.stream().filter(u -> "STUDENT".equals(u.getRole())).count());
        data.put("enabledUsers", users.stream().filter(u -> "ENABLED".equals(u.getStatus())).count());
        data.put("disabledUsers", users.stream().filter(u -> "DISABLED".equals(u.getStatus())).count());
        data.put("doctors", doctorMapper.findAll().size());
        data.put("articles", articleMapper.findAll().size());
        data.put("appointments", appointments.size());
        data.put("pendingAppointments", appointments.stream().filter(a -> "PENDING".equals(a.get("STATUS")) || "PENDING".equals(a.get("status"))).count());
        data.put("consultations", consultations.size());
        data.put("waitingConsultations", consultations.stream().filter(c -> c.get("doctorReply") == null).count());
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

    @PostMapping("/users")
    public ApiResponse<User> createUser(@RequestBody User user) {
        String validation = validateUser(user, true);
        if (validation != null) return ApiResponse.fail(validation);
        if (userMapper.findByUsername(user.getUsername()) != null) return ApiResponse.fail("账号已存在");
        if (!StringUtils.hasText(user.getStatus())) user.setStatus("ENABLED");
        userMapper.insert(user);
        user.setPassword(null);
        return ApiResponse.ok(user);
    }

    @PutMapping("/users/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        String validation = validateUser(user, false);
        if (validation != null) return ApiResponse.fail(validation);
        userMapper.updateAdmin(user);
        if (StringUtils.hasText(user.getPassword())) userMapper.updatePassword(id, user.getPassword());
        User updated = userMapper.findById(id);
        updated.setPassword(null);
        return ApiResponse.ok(updated);
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
        if (doctor.getUserId() == null) return ApiResponse.fail("请选择医生账号");
        if (!StringUtils.hasText(doctor.getDepartment()) || !StringUtils.hasText(doctor.getTitle()) || !StringUtils.hasText(doctor.getSpecialty()) || !StringUtils.hasText(doctor.getScheduleText())) return ApiResponse.fail("请填写科室、职称、擅长方向和坐诊时间");
        if (doctorMapper.findByUserId(doctor.getUserId()) != null) return ApiResponse.fail("该医生账号已有医生档案");
        if (doctor.getStatus() == null) doctor.setStatus("ENABLED");
        doctorMapper.insert(doctor);
        return ApiResponse.ok(doctor);
    }

    @PutMapping("/doctors/{id}")
    public ApiResponse<String> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        doctor.setId(id);
        doctorMapper.update(doctor);
        return ApiResponse.ok("医生档案已更新");
    }

    @GetMapping("/articles")
    public ApiResponse<List<HealthArticle>> articles() { return ApiResponse.ok(articleMapper.findAll()); }
    @PostMapping("/articles")
    public ApiResponse<HealthArticle> createArticle(@RequestBody HealthArticle article) {
        if (!StringUtils.hasText(article.getStatus())) article.setStatus("PUBLISHED");
        articleMapper.insert(article);
        return ApiResponse.ok(article);
    }
    @PutMapping("/articles/{id}")
    public ApiResponse<String> updateArticle(@PathVariable Long id, @RequestBody HealthArticle article) {
        article.setId(id);
        articleMapper.update(article);
        return ApiResponse.ok("科普内容已更新");
    }
    @PutMapping("/articles/{id}/status")
    public ApiResponse<String> updateArticleStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        articleMapper.updateStatus(id, body.getOrDefault("status", "PUBLISHED"));
        return ApiResponse.ok("科普状态已更新");
    }

    @GetMapping("/appointments")
    public ApiResponse<List<Map<String, Object>>> appointments() { return ApiResponse.ok(appointmentMapper.findAll()); }
    @PutMapping("/appointments/{id}/status")
    public ApiResponse<String> updateAppointmentStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        appointmentMapper.updateStatus(id, body.getOrDefault("status", "PENDING"));
        return ApiResponse.ok("预约状态已更新");
    }
    @GetMapping("/consultations")
    public ApiResponse<List<Map<String, Object>>> consultations() { return ApiResponse.ok(consultationMapper.findAllWithStudent()); }

    @GetMapping("/medicines")
    public ApiResponse<List<Medicine>> medicines() { return ApiResponse.ok(medicineMapper.findAll()); }
    @PostMapping("/medicines")
    public ApiResponse<Medicine> createMedicine(@RequestBody Medicine medicine) {
        if (!StringUtils.hasText(medicine.getStatus())) medicine.setStatus("PUBLISHED");
        medicineMapper.insert(medicine);
        return ApiResponse.ok(medicine);
    }
    @PutMapping("/medicines/{id}")
    public ApiResponse<String> updateMedicine(@PathVariable Long id, @RequestBody Medicine medicine) {
        medicine.setId(id);
        medicineMapper.update(medicine);
        return ApiResponse.ok("药品信息已更新");
    }
    @PutMapping("/medicines/{id}/status")
    public ApiResponse<String> updateMedicineStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        medicineMapper.updateStatus(id, body.getOrDefault("status", "PUBLISHED"));
        return ApiResponse.ok("药品状态已更新");
    }

    @GetMapping("/announcements")
    public ApiResponse<List<Announcement>> announcements() { return ApiResponse.ok(announcementMapper.findAll()); }
    @PostMapping("/announcements")
    public ApiResponse<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        if (!StringUtils.hasText(announcement.getStatus())) announcement.setStatus("PUBLISHED");
        announcementMapper.insert(announcement);
        return ApiResponse.ok(announcement);
    }
    @PutMapping("/announcements/{id}")
    public ApiResponse<String> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        announcementMapper.update(announcement);
        return ApiResponse.ok("公告已更新");
    }
    @PutMapping("/announcements/{id}/status")
    public ApiResponse<String> updateAnnouncementStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        announcementMapper.updateStatus(id, body.getOrDefault("status", "PUBLISHED"));
        return ApiResponse.ok("公告状态已更新");
    }

    private String validateUser(User user, boolean creating) {
        if (creating && !StringUtils.hasText(user.getUsername())) return "请填写账号";
        if (creating && !StringUtils.hasText(user.getPassword())) return "请填写初始密码";
        if (!StringUtils.hasText(user.getRealName())) return "请填写姓名";
        if (!StringUtils.hasText(user.getRole())) return "请选择角色";
        if ("STUDENT".equals(user.getRole()) && !StringUtils.hasText(user.getCollege())) return "学生账号请填写学院";
        return null;
    }
}