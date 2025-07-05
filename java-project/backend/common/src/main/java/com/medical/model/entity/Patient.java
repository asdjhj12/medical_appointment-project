package com.medical.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patient")
public class Patient extends BaseEntity {
    private Long userId;
    private String patientName;
    private Integer gender;
    private Integer age;
    private String phone;
    private String idCard;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private String medicalHistory;
    private String allergies;
    private String bloodType;
    private String remark;
} 