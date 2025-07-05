package com.medical.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.model.dto.UserQueryDTO;
import com.medical.model.dto.UserUpdateDTO;
import com.medical.model.vo.UserVO;

/**
 * 管理员用户服务接口
 */
public interface AdminUserService {
    
    /**
     * 获取用户列表
     * @param queryDTO 查询参数
     * @return 用户列表分页数据
     */
    Page<UserVO> getUserList(UserQueryDTO queryDTO);
    
    /**
     * 获取用户详情
     * @param id 用户ID
     * @return 用户详情
     */
    UserVO getUserById(Long id);
    
    /**
     * 创建用户
     * @param updateDTO 用户信息
     */
    void createUser(UserUpdateDTO updateDTO);
    
    /**
     * 更新用户
     * @param id 用户ID
     * @param updateDTO 用户信息
     */
    void updateUser(Long id, UserUpdateDTO updateDTO);
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);
    
    /**
     * 重置密码
     * @param id 用户ID
     */
    void resetPassword(Long id);
    
    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);
} 