package com.medical.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("doctor")
public class Doctor {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String doctorName;
    
    private Long departmentId;
    
    private String title;
    
    private String specialty;
    
    private String introduction;
    
    private Integer gender;
    
    private String phone;
    
    private String idCard;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private User user;
    
    @TableField(exist = false)
    private Department department;
} 