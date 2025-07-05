package com.medical.model.vo;

import lombok.Data;

@Data
public class PatientVO {
    private Long id;
    private Long userId;
    private String realName;
    private String phone;
    private String email;
    private Integer gender;
    private Integer age;
    private String idCard;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private String medicalHistory;
    private String allergies;
    private String bloodType;
    private String remark;
} 