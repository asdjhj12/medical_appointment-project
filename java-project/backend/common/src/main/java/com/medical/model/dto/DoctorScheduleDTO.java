package com.medical.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class DoctorScheduleDTO {
    private Long id;
    
    @NotNull(message = "医生ID不能为空")
    private Long doctorId;
    
    private Long departmentId;
    
    @NotNull(message = "排班日期不能为空")
    private LocalDate scheduleDate;
    
    @NotNull(message = "时段不能为空")
    private Integer period;
    
    @NotNull(message = "最大预约数不能为空")
    private Integer maxAppointments;
    
    private Integer currentAppointments;
    
    private Integer status; // 0:停诊 1:正常
    
    private String description;
    
    // 非数据库字段，用于展示
    private String doctorName;
    private String departmentName;
} 