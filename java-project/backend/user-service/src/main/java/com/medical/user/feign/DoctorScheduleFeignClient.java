package com.medical.user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "doctor-service", url = "http://localhost:8082", path = "/api/doctor-schedules", configuration = FeignConfig.class)
public interface DoctorScheduleFeignClient {
    @PostMapping("/{id}/increment-appointments")
    void incrementCurrentAppointments(@PathVariable("id") Long id);
    
    @PostMapping("/{id}/decrement-appointments")
    void decrementCurrentAppointments(@PathVariable("id") Long id);
} 