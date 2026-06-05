package com.campus.health.controller;

import com.campus.health.common.ApiResponse;
import com.campus.health.dto.AppointmentRequest;
import com.campus.health.entity.Appointment;
import com.campus.health.mapper.AppointmentMapper;
import java.util.List;
import java.util.Map;
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
    private final AppointmentMapper appointmentMapper;

    public AppointmentController(AppointmentMapper appointmentMapper) {
        this.appointmentMapper = appointmentMapper;
    }

    @PostMapping
    public ApiResponse<Appointment> create(@RequestBody AppointmentRequest req) {
        Appointment appointment = new Appointment();
        appointment.setStudentId(req.getStudentId() == null ? 1L : req.getStudentId());
        appointment.setDoctorId(req.getDoctorId() == null ? 1L : req.getDoctorId());
        appointment.setAppointmentDate(req.getAppointmentDate());
        appointment.setTimeSlot(req.getTimeSlot());
        appointment.setReason(req.getReason());
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

    @PutMapping("/{id}/status")
    public ApiResponse<String> status(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "FINISHED");
        appointmentMapper.updateStatus(id, status);
        return ApiResponse.ok("预约状态已更新");
    }
}
