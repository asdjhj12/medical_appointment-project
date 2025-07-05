package com.medical.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DoctorUpdateDTO {
    @NotBlank(message = "医生姓名不能为空")
    private String doctorName;
    
    @NotNull(message = "科室ID不能为空")
    private Long departmentId;
    
    @NotBlank(message = "职称不能为空")
    private String title;

    // 兼容旧代码
    private String department;

    @NotBlank(message = "专长不能为空")
    private String specialty;

    private String introduction;

    // @NotNull(message = "最大接诊人数不能为空")
    // @Positive(message = "最大接诊人数必须大于0")
    // private Integer maxPatients;

    @NotNull(message = "状态不能为空")
    private Integer status;
    
    // 可选字段
    private Integer gender;
    private String phone;
    private String idCard;
    
    // 用户相关字段
    private String username; // 用户名
    private String password; // 密码
} 