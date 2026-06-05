package com.campus.health.entity;
import lombok.Data;
@Data public class Appointment{private Long id;private Long studentId;private Long doctorId;private String appointmentDate;private String timeSlot;private String reason;private String status;}
