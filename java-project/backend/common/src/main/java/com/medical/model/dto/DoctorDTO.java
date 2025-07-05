package com.medical.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class DoctorDTO {
    private Long id;
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotNull(message = "科室ID不能为空")
    private Long departmentId;
    
    @NotBlank(message = "职称不能为空")
    @Size(max = 50, message = "职称长度不能超过50个字符")
    private String title;
    
    @Size(max = 100, message = "专长长度不能超过100个字符")
    private String specialty;
    
    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String introduction;
    
    private Integer status;
    
    // 医生姓名字段，从doctor表的doctor_name字段获取
    private String doctorName;
    
    // 非数据库字段，用于展示（兼容旧代码）
    private String userName;
    private String departmentName;
} 