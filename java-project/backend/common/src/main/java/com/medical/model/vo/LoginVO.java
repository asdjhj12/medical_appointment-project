package com.medical.model.vo;

import lombok.Data;

@Data
public class LoginVO {
    /**
     * 用户信息
     */
    private UserVO user;

    /**
     * 访问令牌
     */
    private String token;
} 