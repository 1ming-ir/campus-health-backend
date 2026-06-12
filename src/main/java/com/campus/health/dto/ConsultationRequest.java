package com.campus.health.dto;

import lombok.Data;

@Data
public class ConsultationRequest {
    private Long studentId;
    private String symptom;
    private String duration;
    private String severity;
    private String medicineUsed;
    private String medicineName;
}