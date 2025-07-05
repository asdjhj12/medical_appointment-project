package com.medical.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("schedule")
public class DoctorSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long doctorId;
    
    private Long departmentId;
    
    private LocalDate scheduleDate;
    
    private Integer period; // 时段（1-上午，2-下午，3-晚上）
    
    private Integer maxAppointments;
    
    private Integer currentAppointments;
    
    private Integer status; // 0:停诊 1:正常
    
    @TableField(exist = false)
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private Doctor doctor;
    
    @TableField(exist = false)
    private String doctorName;
    
    @TableField(exist = false)
    private String departmentName;
} 