package com.campus.health.entity;

import lombok.Data;

@Data
public class Announcement {
    private Long id;
    private String title;
    private String type;
    private String content;
    private String status;
}