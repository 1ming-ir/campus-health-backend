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
        if (req.getDoctorId() == null) {
            return ApiResponse.fail("璇烽€夋嫨鍖荤敓");
        }
        if (!StringUtils.hasText(req.getAppointmentDate())) {
            return ApiResponse.fail("璇烽€夋嫨棰勭害鏃ユ湡");
        }
        if (!StringUtils.hasText(req.getTimeSlot())) {
            return ApiResponse.fail("璇烽€夋嫨棰勭害鏃堕棿娈?);
        }
        if (!StringUtils.hasText(req.getReason())) {
            return ApiResponse.fail("璇峰～鍐欓绾﹀師鍥?);
        }
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
        if (!ALLOWED_STATUS.contains(status)) {
            return ApiResponse.fail("涓嶆敮鎸佺殑棰勭害鐘舵€?);
        }
        appointmentMapper.updateStatus(id, status);
        return ApiResponse.ok("棰勭害鐘舵€佸凡鏇存柊");
    }
}