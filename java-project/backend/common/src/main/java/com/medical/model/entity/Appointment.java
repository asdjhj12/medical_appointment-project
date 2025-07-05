package com.medical.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("appointment")
public class Appointment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long patientId;
    
    private Long doctorId;
    
    private Long scheduleId;
    
    private String appointmentNumber;
    
    private Integer status; // 0:待处理 1:已确认 2:已完成 3:已取消 4:已过期
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private User user;
    
    @TableField(exist = false)
    private Doctor doctor;
    
    @TableField(exist = false)
    private DoctorSchedule schedule;
} 