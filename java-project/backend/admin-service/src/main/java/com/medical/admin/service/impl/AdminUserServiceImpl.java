package com.medical.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.admin.service.AdminUserService;
import com.medical.common.exception.BusinessException;
import com.medical.mapper.UserMapper;
import com.medical.mapper.DoctorMapper;
import com.medical.model.dto.UserQueryDTO;
import com.medical.model.dto.UserUpdateDTO;
import com.medical.model.entity.User;
import com.medical.model.entity.Doctor;
import com.medical.model.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final DoctorMapper doctorMapper;

    @Override
    public Page<UserVO> getUserList(UserQueryDTO queryDTO) {
        Page<User> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件
        if (StringUtils.isNotBlank(queryDTO.getUsername())) {
            queryWrapper.like(User::getUsername, queryDTO.getUsername());
        }
        if (StringUtils.isNotBlank(queryDTO.getName())) {
            queryWrapper.like(User::getRealName, queryDTO.getName());
        }
        if (StringUtils.isNotBlank(queryDTO.getPhone())) {
            queryWrapper.like(User::getPhone, queryDTO.getPhone());
        }
        if (StringUtils.isNotBlank(queryDTO.getRole())) {
            queryWrapper.eq(User::getRole, queryDTO.getRole());
        }
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(User::getStatus, queryDTO.getStatus());
        }
        
        // 查询
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);
        
        // 转换结果
        Page<UserVO> resultPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> records = userPage.getRecords().stream()
                .map(user -> {
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(user, userVO);
                    return userVO;
                })
                .collect(Collectors.toList());
        resultPage.setRecords(records);
        
        return resultPage;
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    @Transactional
    public void createUser(UserUpdateDTO updateDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, updateDTO.getUsername());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(updateDTO, user);
        
        // 加密密码
        if (StringUtils.isNotBlank(updateDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        } else {
            // 默认密码
            user.setPassword(passwordEncoder.encode("123456"));
        }
        
        userMapper.insert(user);
        
        // 如果角色是医生，同时在doctor表中创建记录
        if ("DOCTOR".equals(updateDTO.getRole())) {
            Doctor doctor = new Doctor();
            doctor.setUserId(user.getId());
            doctor.setDoctorName(updateDTO.getRealName());
            doctor.setPhone(updateDTO.getPhone());
            doctor.setStatus(updateDTO.getStatus());
            doctor.setGender(updateDTO.getGender());
            // 设置默认科室ID，这里假设1是一个有效的科室ID
            doctor.setDepartmentId(1L);
            // 设置默认职称
            doctor.setTitle("医师");
            // 设置默认专长
            doctor.setSpecialty("全科");
            
            // 插入医生记录
            doctorMapper.insert(doctor);
            log.info("创建医生记录成功，医生ID: {}, 用户ID: {}", doctor.getId(), user.getId());
        }
    }

    @Override
    @Transactional
    public void updateUser(Long id, UserUpdateDTO updateDTO) {
        // 检查用户是否存在
        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 如果修改用户名，检查用户名是否已存在
        if (StringUtils.isNotBlank(updateDTO.getUsername()) && !updateDTO.getUsername().equals(existingUser.getUsername())) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, updateDTO.getUsername());
            if (userMapper.selectCount(queryWrapper) > 0) {
                throw new BusinessException("用户名已存在");
            }
        }
        
        // 更新用户
        User user = new User();
        BeanUtils.copyProperties(updateDTO, user);
        user.setId(id);
        
        // 如果有密码，加密密码
        if (StringUtils.isNotBlank(updateDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        } else {
            // 不修改密码
            user.setPassword(null);
        }
        
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        // 检查用户是否存在
        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        userMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void resetPassword(Long id) {
        // 检查用户是否存在
        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 重置密码为123456
        User user = new User();
        user.setId(id);
        user.setPassword(passwordEncoder.encode("123456"));
        
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        // 检查用户是否存在
        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 更新状态
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        
        userMapper.updateById(user);
    }
} 