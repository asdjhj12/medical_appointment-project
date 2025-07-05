package com.medical.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.model.dto.UserLoginDTO;
import com.medical.model.dto.UserQueryDTO;
import com.medical.model.dto.UserRegisterDTO;
import com.medical.model.dto.UserUpdateDTO;
import com.medical.model.vo.LoginVO;
import com.medical.model.vo.UserVO;

public interface UserService {
    /**
     * 用户登录
     */
    LoginVO login(UserLoginDTO loginDTO);

    /**
     * 获取用户列表
     */
    Page<UserVO> getUserList(UserQueryDTO queryDTO);

    /**
     * 获取用户详情
     */
    UserVO getUserById(Long id);

    /**
     * 创建用户
     */
    void createUser(UserUpdateDTO updateDTO);

    /**
     * 更新用户
     */
    void updateUser(Long id, UserUpdateDTO updateDTO);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 修改密码
     */
    void updatePassword(Long id, String oldPassword, String newPassword);

    /**
     * 重置密码
     */
    void resetPassword(Long id);

    /**
     * 更新状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 用户注册
     */
    void register(UserRegisterDTO registerDTO);

    /**
     * 更新用户头像
     */
    void updateAvatar(Long id, String avatarUrl);

    /**
     * 获取当前登录用户
     */
    UserVO getCurrentUser();
} 