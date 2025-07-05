package com.medical.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.Result;
import com.medical.model.dto.PatientQueryDTO;
import com.medical.model.dto.PatientUpdateDTO;
import com.medical.model.vo.PatientVO;
import com.medical.user.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping("/list")
    public Result<Page<PatientVO>> getPatientList(PatientQueryDTO queryDTO) {
        return Result.success(patientService.getPatientList(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<PatientVO> getPatientById(@PathVariable Long id) {
        return Result.success(patientService.getPatientById(id));
    }

    @GetMapping("/user/{userId}")
    public Result<PatientVO> getOrRegisterPatientByUserId(@PathVariable Long userId) {
        PatientVO patient = null;
        try {
            patient = patientService.getPatientByUserId(userId);
        } catch (Exception e) {
            // 没有就自动注册
            patientService.registerByUserId(userId);
            patient = patientService.getPatientByUserId(userId);
        }
        return Result.success(patient);
    }

    @PutMapping("/{id}")
    public Result<Void> updatePatient(@PathVariable Long id, @RequestBody @Valid PatientUpdateDTO updateDTO) {
        patientService.updatePatient(id, updateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return Result.success();
    }

    @PostMapping("/registerByUserId/{userId}")
    public Result<Void> registerByUserId(@PathVariable Long userId) {
        patientService.registerByUserId(userId);
        return Result.success();
    }

    @GetMapping("/count")
    public Result<Long> getPatientCount() {
        long count = patientService.count(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.medical.model.entity.Patient>().eq("deleted", 0));
        return Result.success(count);
    }
} 