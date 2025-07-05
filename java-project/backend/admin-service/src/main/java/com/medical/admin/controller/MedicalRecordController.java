package com.medical.admin.controller;

import com.medical.admin.service.MedicalRecordService;
import com.medical.common.api.Result;
import com.medical.model.dto.MedicalRecordDTO;
import com.medical.model.dto.TakeMedicineDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "电子病历管理")
@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @ApiOperation("创建电子病历")
    @PostMapping
    public Result<Void> createMedicalRecord(@Validated @RequestBody MedicalRecordDTO medicalRecordDTO) {
        try {
            System.out.println("接收到创建病历请求: " + medicalRecordDTO);
            medicalRecordService.createMedicalRecord(medicalRecordDTO);
            System.out.println("病历创建成功");
            return Result.success();
        } catch (Exception e) {
            System.out.println("病历创建失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @ApiOperation("根据ID获取电子病历详情（用户端/医生端通用）")
    @GetMapping("/detail/{id}")
    public Result<MedicalRecordDTO> getMedicalRecordDetailForUser(@PathVariable Long id) {
        return Result.success(medicalRecordService.getMedicalRecordById(id));
    }

    @ApiOperation("根据患者ID获取电子病历列表")
    @GetMapping("/patient/{patientId}")
    public Result<List<MedicalRecordDTO>> getMedicalRecordsByPatientId(@PathVariable Long patientId) {
        return Result.success(medicalRecordService.getMedicalRecordsByPatientId(patientId));
    }

    @ApiOperation("根据医生ID获取电子病历列表")
    @GetMapping("/doctor/{doctorId}")
    public Result<List<MedicalRecordDTO>> getMedicalRecordsByDoctorId(@PathVariable Long doctorId) {
        return Result.success(medicalRecordService.getMedicalRecordsByDoctorId(doctorId));
    }

    @ApiOperation("更新电子病历")
    @PutMapping
    public Result<Void> updateMedicalRecord(@Validated @RequestBody MedicalRecordDTO medicalRecordDTO) {
        medicalRecordService.updateMedicalRecord(medicalRecordDTO);
        return Result.success();
    }

    @ApiOperation("删除电子病历")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMedicalRecord(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecord(id);
        return Result.success();
    }

    @ApiOperation("用户取药")
    @PostMapping("/take-medicine")
    public Result<Void> takeMedicine(@RequestBody TakeMedicineDTO dto) {
        medicalRecordService.takeMedicine(dto);
        return Result.success();
    }

    @ApiOperation("获取所有电子病历（管理员用）")
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<MedicalRecordDTO>> getAllMedicalRecords() {
        return Result.success(medicalRecordService.getAllMedicalRecords());
    }

    // 可以根据需要添加分页查询等其他接口
} 