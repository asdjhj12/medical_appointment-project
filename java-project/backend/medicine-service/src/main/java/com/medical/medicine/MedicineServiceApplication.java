package com.medical.medicine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
        "com.medical.medicine",           // 本服务包
        "com.medical.config",             // 公共配置
        "com.medical.security",           // 公共安全
        "com.medical.utils",              // 公共工具
        "com.medical.medicine_inventory", // 药品库存服务
        "com.medical.medicine_stock_records", // 药品库存记录服务
        "com.medical.doctor"              // 医生服务
})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.medical.mapper")
public class MedicineServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedicineServiceApplication.class, args);
    }
} 