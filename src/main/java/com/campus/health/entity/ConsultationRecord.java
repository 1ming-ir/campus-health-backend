package com.campus.health.entity;
import lombok.Data;
@Data public class ConsultationRecord{private Long id;private Long studentId;private String symptom;private String duration;private String severity;private String medicineUsed;private String aiAdvice;private String doctorReply;private String status;}
