package com.campus.health.entity;

import lombok.Data;

@Data
public class Doctor {
    private Long id;
    private Long userId;
    private String realName;
    private String department;
    private String title;
    private String specialty;
    private String scheduleText;
}
