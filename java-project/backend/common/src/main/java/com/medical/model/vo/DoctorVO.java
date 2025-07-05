package com.medical.model.vo;

import lombok.Data;

@Data
public class DoctorVO {
    private Long id;
    private Long userId;
    private String username;
    private String password;
    private String realName;
    private Integer gender;
    private Integer age;
    private String phone;
    private String email;
    private String avatar;
    private Long departmentId;
    private String departmentName;
    private String title;
    private String introduction;
    private String specialty;
    private Integer status;
    private String department;
} 