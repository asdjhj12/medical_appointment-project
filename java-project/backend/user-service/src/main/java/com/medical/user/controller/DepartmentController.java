package com.medical.user.controller;

import com.medical.common.api.Result;
import com.medical.user.service.DepartmentService;
import com.medical.model.dto.DepartmentDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "科室管理")
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @ApiOperation("获取所有科室")
    @GetMapping
    public Result<List<DepartmentDTO>> listAll() {
        return Result.success(departmentService.listAll());
    }

    @ApiOperation("获取科室详情")
    @GetMapping("/{id}")
    public Result<DepartmentDTO> getById(@PathVariable Long id) {
        return Result.success(departmentService.getById(id));
    }

    @ApiOperation("创建科室")
    @PostMapping
    public Result<Void> create(@Validated @RequestBody DepartmentDTO departmentDTO) {
        departmentService.create(departmentDTO);
        return Result.success();
    }

    @ApiOperation("更新科室")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Validated @RequestBody DepartmentDTO departmentDTO) {
        departmentDTO.setId(id);
        departmentService.update(departmentDTO);
        return Result.success();
    }

    @ApiOperation("删除科室")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return Result.success();
    }

    @ApiOperation("更新科室状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        departmentService.updateStatus(id, status);
        return Result.success();
    }
} 