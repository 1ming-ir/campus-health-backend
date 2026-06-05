package com.campus.health.controller;

import com.campus.health.common.ApiResponse;
import com.campus.health.dto.ConsultationRequest;
import com.campus.health.entity.ConsultationRecord;
import com.campus.health.mapper.ConsultationRecordMapper;
import com.campus.health.service.AiConsultationService;
import java.util.HashMap;
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
@RequestMapping("/api/consultations")
public class ConsultationController {
    private final AiConsultationService ai;
    private final ConsultationRecordMapper consultationMapper;

    public ConsultationController(AiConsultationService ai, ConsultationRecordMapper consultationMapper) {
        this.ai = ai;
        this.consultationMapper = consultationMapper;
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody ConsultationRequest req) {
        String advice = ai.generateAdvice(req);
        ConsultationRecord record = new ConsultationRecord();
        record.setStudentId(req.getStudentId() == null ? 1L : req.getStudentId());
        record.setSymptom(req.getSymptom());
        record.setDuration(req.getDuration());
        record.setSeverity(req.getSeverity());
        record.setMedicineUsed(req.getMedicineUsed());
        record.setAiAdvice(advice);
        record.setStatus("CREATED");
        consultationMapper.insert(record);

        Map<String, Object> data = new HashMap<>();
        data.put("id", record.getId());
        data.put("aiAdvice", advice);
        return ApiResponse.ok(data);
    }

    @GetMapping("/my")
    public ApiResponse<List<ConsultationRecord>> my(@RequestParam(defaultValue = "1") Long studentId) {
        return ApiResponse.ok(consultationMapper.findByStudentId(studentId));
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        return ApiResponse.ok(consultationMapper.findAllWithStudent());
    }

    @PutMapping("/{id}/reply")
    public ApiResponse<String> reply(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reply = body.getOrDefault("doctorReply", body.getOrDefault("reply", "建议到校医院进一步检查。"));
        consultationMapper.updateDoctorReply(id, reply);
        return ApiResponse.ok("医生建议已保存");
    }
}
