package com.medical.user.feign;

import com.medical.model.dto.MedicalRecordDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "admin-service", url = "http://localhost:8083", path = "/api/medical-records", configuration = FeignConfig.class)
public interface MedicalRecordFeignClient {
    @PostMapping
    void createMedicalRecord(@RequestBody MedicalRecordDTO record);
} 