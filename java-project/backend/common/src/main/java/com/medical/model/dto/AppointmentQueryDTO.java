package com.medical.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class AppointmentQueryDTO {
    private Long userId;
    private Long doctorId;
    private Long departmentId;
    private Integer status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;
    private String appointmentNumber;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
} 