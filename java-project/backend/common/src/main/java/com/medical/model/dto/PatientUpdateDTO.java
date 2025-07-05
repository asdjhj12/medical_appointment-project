package com.medical.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class PatientUpdateDTO {
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$", message = "身份证号格式不正确")
    private String idCard;

    @NotBlank(message = "地址不能为空")
    private String address;

    @NotBlank(message = "紧急联系人不能为空")
    private String emergencyContact;

    @NotBlank(message = "紧急联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String emergencyPhone;

    private String medicalHistory;
    private String allergies;
    private String bloodType;
    private String remark;
} 