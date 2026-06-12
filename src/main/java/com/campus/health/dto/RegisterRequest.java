package com.campus.health.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String realName;
    private String college;
    private String className;
    private String phone;
}