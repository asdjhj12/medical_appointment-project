package com.medical.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.Result;
import com.medical.model.dto.UpdatePasswordParams;
import com.medical.model.dto.UserLoginDTO;
import com.medical.model.dto.UserQueryDTO;
import com.medical.model.dto.UserRegisterDTO;
import com.medical.model.dto.UserUpdateDTO;
import com.medical.model.vo.LoginVO;
import com.medical.model.vo.UserVO;
import com.medical.user.service.UserService;
import com.medical.user.service.AppointmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final AppointmentService appointmentService;

    public UserController(UserService userService, 
                         @Qualifier("userAppointmentServiceImpl") AppointmentService appointmentService) {
        this.userService = userService;
        this.appointmentService = appointmentService;
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody UserLoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/info")
    public Result<UserVO> getCurrentUserInfo() {
        return Result.success(userService.getCurrentUser());
    }

    @ApiOperation("获取今日预约数量")
    @GetMapping("/today/count")
    public Result<Long> getTodayAppointmentsCount() {
        return Result.success(appointmentService.getTodayAppointmentsCount());
    }

    @ApiOperation("获取待诊患者数量")
    @GetMapping("/waiting/count")
    public Result<Long> getWaitingPatientsCount() {
        return Result.success(appointmentService.getWaitingPatientsCount());
    }
    @ApiOperation("更新当前登录用户信息")
    @PutMapping("/info")
    public Result<Void> updateCurrentUserInfo(@RequestBody UserUpdateDTO updateDTO) {
        // 获取当前用户ID
        Long userId = userService.getCurrentUser().getId();

        // 简化参数验证
        // 用户名、角色和状态不应该由普通用户更改
        updateDTO.setUsername(null);
        updateDTO.setRole(null);
        updateDTO.setStatus(null);

        userService.updateUser(userId, updateDTO);
        return Result.success();
    }

    @ApiOperation("修改当前登录用户密码")
    @PutMapping("/password")
    public Result<Void> updateCurrentUserPassword(@RequestBody UpdatePasswordParams params) {
        // 获取当前用户ID
        Long userId = userService.getCurrentUser().getId();
        userService.updatePassword(userId, params.getOldPassword(), params.getNewPassword());
        return Result.success();
    }
}