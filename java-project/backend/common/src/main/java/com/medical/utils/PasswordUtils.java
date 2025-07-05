package com.medical.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordUtils {
    private final PasswordEncoder passwordEncoder;

    /**
     * 加密密码
     */
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 校验密码
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
} 