package com.medical.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.admin.service.AdminUserService;
import com.medical.common.Result;
import com.medical.model.dto.UserQueryDTO;
import com.medical.model.dto.UserUpdateDTO;
import com.medical.model.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员用户管理")
@RestController
@RequestMapping("/api/admin-users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @ApiOperation("获取用户列表")
    @GetMapping
    public Result<Page<UserVO>> getUserList(UserQueryDTO queryDTO) {
        return Result.success(adminUserService.getUserList(queryDTO));
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        return Result.success(adminUserService.getUserById(id));
    }

    @ApiOperation("创建用户")
    @PostMapping
    public Result<Void> createUser(@Validated @RequestBody UserUpdateDTO updateDTO) {
        adminUserService.createUser(updateDTO);
        return Result.success();
    }

    @ApiOperation("更新用户")
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @Validated @RequestBody UserUpdateDTO updateDTO) {
        adminUserService.updateUser(id, updateDTO);
        return Result.success();
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return Result.success();
    }

    @ApiOperation("重置密码")
    @PutMapping("/{id}/password/reset")
    public Result<Void> resetPassword(@PathVariable Long id) {
        adminUserService.resetPassword(id);
        return Result.success();
    }

    @ApiOperation("更新用户状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminUserService.updateStatus(id, status);
        return Result.success();
    }
}