package com.campus.health.entity;

import lombok.Data;

@Data
public class Medicine {
    private Long id;
    private String name;
    private String category;
    private String usageInfo;
    private String caution;
    private String prescriptionRequired;
    private String status;
}