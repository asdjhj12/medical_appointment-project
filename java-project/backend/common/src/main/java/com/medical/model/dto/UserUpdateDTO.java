package com.medical.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UserUpdateDTO {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String realName;

    /**
     * 性别（0-女，1-男）
     */
    @NotNull(message = "性别不能为空")
    private Integer gender;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色（ADMIN-管理员，DOCTOR-医生，PHARMACIST-药剂师，RECEPTIONIST-前台）
     */
    @NotBlank(message = "角色不能为空")
    private String role;

    /**
     * 状态（0-禁用，1-启用）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
} 