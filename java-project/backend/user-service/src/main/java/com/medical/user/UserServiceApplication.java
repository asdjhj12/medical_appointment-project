package com.medical.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
    "com.medical.user",           // 用户服务包
    "com.medical.config",         // 公共配置
    "com.medical.security",       // 公共安全
    "com.medical.utils"           // 公共工具
})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.medical.user.feign"})
@MapperScan("com.medical.mapper")
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
} 