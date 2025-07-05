package com.medical.doctor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.api.Result;
import com.medical.doctor.service.DoctorScheduleService;
import com.medical.model.dto.DoctorScheduleDTO;
import com.medical.model.dto.ScheduleQueryDTO;
import com.medical.model.vo.ScheduleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Api(tags = "医生排班管理")
@RestController
@RequestMapping("/api/doctor-schedules")
@RequiredArgsConstructor
public class DoctorScheduleController {
    private final DoctorScheduleService doctorScheduleService;

    @ApiOperation("获取医生排班列表")
    @GetMapping("/doctor/{doctorId}")
    public Result<List<DoctorScheduleDTO>> getScheduleByDoctor(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleDate) {
        return Result.success(doctorScheduleService.getScheduleByDoctor(doctorId, scheduleDate));
    }

    @ApiOperation("获取指定日期的所有排班")
    @GetMapping("/date")
    public Result<List<DoctorScheduleDTO>> getScheduleByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleDate) {
        return Result.success(doctorScheduleService.getScheduleByDate(scheduleDate));
    }

    @ApiOperation("创建排班")
    @PostMapping
    public Result<Void> create(@Validated @RequestBody DoctorScheduleDTO scheduleDTO) {
        doctorScheduleService.create(scheduleDTO);
        return Result.success();
    }

    @ApiOperation("更新排班")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Validated @RequestBody DoctorScheduleDTO scheduleDTO) {
        scheduleDTO.setId(id);
        doctorScheduleService.update(scheduleDTO);
        return Result.success();
    }

    @ApiOperation("删除排班")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        doctorScheduleService.delete(id);
        return Result.success();
    }

    @ApiOperation("更新排班状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        doctorScheduleService.updateStatus(id, status);
        return Result.success();
    }

    @ApiOperation("获取排班列表")
    @GetMapping
    public Result<Page<ScheduleVO>> getScheduleList(ScheduleQueryDTO queryDTO) {
        return Result.success(doctorScheduleService.getScheduleList(queryDTO));
    }
} 