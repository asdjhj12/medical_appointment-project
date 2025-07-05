package com.medical.doctor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.api.Result;
import com.medical.model.dto.DoctorDTO;
import com.medical.model.dto.DoctorQueryDTO;
import com.medical.model.dto.DoctorUpdateDTO;
import com.medical.model.vo.DoctorVO;
import com.medical.doctor.service.DoctorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "医生管理")
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @ApiOperation("获取医生列表 (分页)")
    @GetMapping
    @PreAuthorize("permitAll()")
    public Result<Page<DoctorVO>> getDoctorList(DoctorQueryDTO queryDTO) {
        return Result.success(doctorService.getDoctorList(queryDTO));
    }

    @ApiOperation("获取医生详情")
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public Result<DoctorVO> getDoctorById(@PathVariable Long id) {
        return Result.success(doctorService.getDoctorById(id));
    }

    @ApiOperation("创建医生")
    @PostMapping
    @PreAuthorize("permitAll()")
    public Result<Void> createDoctor(@Validated @RequestBody DoctorUpdateDTO updateDTO) {
        doctorService.createDoctor(updateDTO);
        return Result.success();
    }

    @ApiOperation("更新医生")
    @PutMapping("/{id}")
    @PreAuthorize("permitAll()")
    public Result<Void> updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorUpdateDTO updateDTO) {
        doctorService.updateDoctor(id, updateDTO);
        return Result.success();
    }

    @ApiOperation("删除医生")
    @DeleteMapping("/{id}")
    @PreAuthorize("permitAll()")
    public Result<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return Result.success();
    }

    @ApiOperation("更新医生状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("permitAll()")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        doctorService.updateStatus(id, status);
        return Result.success();
    }

    @ApiOperation("获取所有医生 (精简信息)")
    @GetMapping("/all")
    public Result<List<DoctorDTO>> listAll() {
        return Result.success(doctorService.listAll());
    }

    @ApiOperation("根据科室获取医生列表 (精简信息)")
    @GetMapping("/department/{departmentId}/list")
    public Result<List<DoctorDTO>> listByDepartment(@PathVariable Long departmentId) {
        return Result.success(doctorService.listByDepartment(departmentId));
    }

    @ApiOperation("获取在诊医生数量")
    @GetMapping("/active/count")
    public Result<Long> getActiveDoctorsCount() {
        return Result.success(doctorService.getActiveDoctorsCount());
    }

    @ApiOperation("获取医生总数")
    @GetMapping("/count")
    public Result<Long> getDoctorCount() {
        long count = doctorService.count(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.medical.model.entity.Doctor>().eq("deleted", 0));
        return Result.success(count);
    }

    @ApiOperation("根据用户ID获取医生信息")
    @GetMapping("/user/{userId}")
    public Result<DoctorVO> getDoctorByUserId(@PathVariable Long userId) {
        return Result.success(doctorService.getDoctorByUserId(userId));
    }
} 