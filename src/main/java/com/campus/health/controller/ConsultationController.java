package com.campus.health.controller;

import com.campus.health.common.ApiResponse;
import com.campus.health.dto.ConsultationRequest;
import com.campus.health.entity.ConsultationRecord;
import com.campus.health.mapper.ConsultationRecordMapper;
import com.campus.health.service.AiConsultationService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
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
        String validation = validate(req);
        if (validation != null) return ApiResponse.fail(validation);
        String advice = ai.generateAdvice(req);
        ConsultationRecord record = new ConsultationRecord();
        record.setStudentId(req.getStudentId() == null ? 1L : req.getStudentId());
        record.setSymptom(req.getSymptom().trim());
        record.setDuration(req.getDuration().trim());
        record.setSeverity(req.getSeverity());
        record.setMedicineUsed(req.getMedicineUsed());
        record.setMedicineName(req.getMedicineName());
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
        String reply = body.getOrDefault("doctorReply", body.getOrDefault("reply", ""));
        Long doctorId = parseLong(body.getOrDefault("doctorId", "1"));
        if (!StringUtils.hasText(reply) || reply.trim().length() < 5) {
            return ApiResponse.fail("医生补充建议至少 5 个字");
        }
        consultationMapper.updateDoctorReply(id, doctorId, cleanMarkdown(reply.trim()));
        return ApiResponse.ok("医生补充建议已保存");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id, @RequestParam(defaultValue = "1") Long studentId) {
        int changed = consultationMapper.deleteUnreplied(id, studentId);
        if (changed == 0) return ApiResponse.fail("已回复记录不能删除，只能归档");
        return ApiResponse.ok("问诊记录已删除");
    }

    @PutMapping("/{id}/archive")
    public ApiResponse<String> archive(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long studentId = body.getOrDefault("studentId", 1L);
        int changed = consultationMapper.archiveReplied(id, studentId);
        if (changed == 0) return ApiResponse.fail("只有已回复记录可以归档");
        return ApiResponse.ok("问诊记录已归档");
    }

    private String validate(ConsultationRequest req) {
        if (!StringUtils.hasText(req.getSymptom()) || req.getSymptom().trim().length() < 5) return "症状描述至少填写 5 个字";
        if (!StringUtils.hasText(req.getDuration())) return "请填写持续时间";
        if (!StringUtils.hasText(req.getSeverity())) return "请选择严重程度";
        if (!StringUtils.hasText(req.getMedicineUsed())) return "请选择是否用药";
        if ("已用药".equals(req.getMedicineUsed()) && !StringUtils.hasText(req.getMedicineName())) return "已用药时请填写药品名称";
        return null;
    }

    private Long parseLong(String value) {
        try { return Long.parseLong(value); } catch (NumberFormatException ex) { return 1L; }
    }

    private String cleanMarkdown(String text) {
        return text.replace("###", "").replace("##", "").replace("**", "").replace("---", "").replace("```", "").trim();
    }
}