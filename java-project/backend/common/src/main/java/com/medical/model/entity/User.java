package com.medical.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String realName;
    private Integer gender;
    private LocalDate birthDate;
    private String idCard;
    private String address;
    private String phone;
    private String email;
    private Integer status;
    private String role;
    private String avatar;
    private String registerIp;
    private LocalDateTime registerTime;
    private Integer isVerified;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    // 非数据库字段，用于计算年龄
    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return java.time.Period.between(birthDate, LocalDate.now()).getYears();
    }
} 