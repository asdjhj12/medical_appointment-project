package com.medical.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AppointmentDTO {
    private Long id;
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotNull(message = "医生ID不能为空")
    private Long doctorId;
    
    @NotNull(message = "排班ID不能为空")
    private Long scheduleId;
    
    private String appointmentNo;
    
    private Integer status;
    
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;
    
    // 非数据库字段，用于展示
    private String userName;
    private String doctorName;
    private String departmentName;
    private String scheduleDate;
    private String scheduleTime;
    private String createTime;
    private String updateTime;
} 