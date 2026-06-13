package com.campus.health.controller;

import com.campus.health.common.ApiResponse;
import com.campus.health.dto.AppointmentRequest;
import com.campus.health.entity.Appointment;
import com.campus.health.mapper.AppointmentMapper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private static final Set<String> ALLOWED_STATUS = Set.of("PENDING", "APPROVED", "REJECTED", "FINISHED", "CANCELLED");
    private final AppointmentMapper appointmentMapper;

    public AppointmentController(AppointmentMapper appointmentMapper) {
        this.appointmentMapper = appointmentMapper;
    }

    @PostMapping
    public ApiResponse<Appointment> create(@RequestBody AppointmentRequest req) {
        if (req.getDoctorId() == null) return ApiResponse.fail("请选择医生");
        if (!StringUtils.hasText(req.getAppointmentDate())) return ApiResponse.fail("请选择预约日期");
        if (!StringUtils.hasText(req.getTimeSlot())) return ApiResponse.fail("请选择时间段");
        if (!StringUtils.hasText(req.getReason()) || req.getReason().trim().length() < 5) return ApiResponse.fail("预约原因至少 5 个字");
        Appointment appointment = new Appointment();
        appointment.setStudentId(req.getStudentId() == null ? 1L : req.getStudentId());
        appointment.setDoctorId(req.getDoctorId());
        appointment.setAppointmentDate(req.getAppointmentDate());
        appointment.setTimeSlot(req.getTimeSlot());
        appointment.setReason(req.getReason().trim());
        appointment.setStatus("PENDING");
        appointmentMapper.insert(appointment);
        return ApiResponse.ok(appointment);
    }

    @GetMapping("/my")
    public ApiResponse<List<Map<String, Object>>> my(@RequestParam(defaultValue = "1") Long studentId) {
        return ApiResponse.ok(appointmentMapper.findByStudentId(studentId));
    }

    @GetMapping("/doctor")
    public ApiResponse<List<Map<String, Object>>> doctor(@RequestParam(defaultValue = "1") Long doctorId) {
        return ApiResponse.ok(appointmentMapper.findForDoctor(doctorId));
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> all() {
        return ApiResponse.ok(appointmentMapper.findAll());
    }

    @PutMapping("/{id}/status")
    public ApiResponse<String> status(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "FINISHED");
        if (!ALLOWED_STATUS.contains(status)) return ApiResponse.fail("无效的预约状态");
        appointmentMapper.updateStatus(id, status);
        return ApiResponse.ok("预约状态已更新");
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<String> cancel(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        appointmentMapper.updateStatus(id, "CANCELLED");
        return ApiResponse.ok("预约已取消");
    }
}