package com.campus.health.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String role;
    private String phone;
    private String status;
    private String college;
    private String className;
    private String gender;
    private Integer age;
    private String allergyHistory;
    private String medicalHistory;
    private String emergencyContact;
}