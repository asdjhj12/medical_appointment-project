package com.medical.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.ApiException;
import com.medical.common.exception.BusinessException;
import com.medical.mapper.UserMapper;
import com.medical.mapper.DoctorMapper;
import com.medical.model.dto.UserLoginDTO;
import com.medical.model.dto.UserQueryDTO;
import com.medical.model.dto.UserRegisterDTO;
import com.medical.model.dto.UserUpdateDTO;
import com.medical.model.entity.User;
import com.medical.model.entity.Doctor;
import com.medical.model.vo.LoginVO;
import com.medical.model.vo.UserVO;
import com.medical.user.service.UserService;
import com.medical.utils.JwtUtils;
import com.medical.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final PasswordUtils passwordUtils;
    private final HttpServletRequest request;
    private final RedisTemplate<String, String> redisTemplate;
    private final DoctorMapper doctorMapper;

    @Override
    public LoginVO login(UserLoginDTO loginDTO) {
        // 查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, loginDTO.getUsername())
        );

        // 校验用户是否存在
        if (user == null) {
            throw new ApiException("用户名或密码错误");
        }

        // 校验密码
        if (!passwordUtils.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new ApiException("用户名或密码错误");
        }

        // 校验状态
        if (user.getStatus() == 0) {
            throw new ApiException("账号已被禁用");
        }

        // 生成token
        String token = jwtUtils.generateToken(user.getId(), user.getRole());

        // 转换VO
        UserVO userVO = convertToVO(user);

        // 返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setUser(userVO);
        loginVO.setToken(token);

        return loginVO;
    }

    @Override
    public Page<UserVO> getUserList(UserQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getUsername()), User::getUsername, queryDTO.getUsername())
                .like(StrUtil.isNotBlank(queryDTO.getName()), User::getRealName, queryDTO.getName())
                .like(StrUtil.isNotBlank(queryDTO.getPhone()), User::getPhone, queryDTO.getPhone())
                .eq(StrUtil.isNotBlank(queryDTO.getRole()), User::getRole, queryDTO.getRole())
                .eq(queryDTO.getStatus() != null, User::getStatus, queryDTO.getStatus())
                .orderByDesc(User::getCreateTime);

        // 查询数据
        Page<User> page = userMapper.selectPage(
                new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()),
                wrapper
        );

        // 转换VO
        Page<UserVO> voPage = new Page<>();
        BeanUtils.copyProperties(page, voPage, "records");
        voPage.setRecords(page.getRecords().stream().map(this::convertToVO).toList());

        return voPage;
    }

    @Override
    public UserVO getUserById(Long id) {
        // 查询用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ApiException("用户不存在");
        }

        // 转换VO
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserUpdateDTO updateDTO) {
        // 校验用户名是否存在
        if (checkUsernameExists(updateDTO.getUsername())) {
            throw new ApiException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(updateDTO, user);
        user.setPassword(passwordUtils.encode(updateDTO.getPassword()));
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
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UserUpdateDTO updateDTO) {
        // 查询用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ApiException("用户不存在");
        }

        // 校验用户名是否存在
        if (updateDTO.getUsername() != null && !user.getUsername().equals(updateDTO.getUsername()) && checkUsernameExists(updateDTO.getUsername())) {
            throw new ApiException("用户名已存在");
        }

        // 更新用户
        BeanUtils.copyProperties(updateDTO, user);
        if (StrUtil.isNotBlank(updateDTO.getPassword())) {
            user.setPassword(passwordUtils.encode(updateDTO.getPassword()));
        }
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        // 查询用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ApiException("用户不存在");
        }

        // 删除用户
        userMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        // 查询用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ApiException("用户不存在");
        }

        // 校验旧密码
        if (!passwordUtils.matches(oldPassword, user.getPassword())) {
            throw new ApiException("旧密码错误");
        }

        // 更新密码
        user.setPassword(passwordUtils.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id) {
        // 查询用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ApiException("用户不存在");
        }

        // 重置密码
        user.setPassword(passwordUtils.encode("123456"));
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        // 查询用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ApiException("用户不存在");
        }

        // 更新状态
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterDTO registerDTO) {
        // 校验用户名是否存在
        if (checkUsernameExists(registerDTO.getUsername())) {
            throw new ApiException("用户名已存在");
        }

        // 校验手机号是否存在
        if (checkPhoneExists(registerDTO.getPhone())) {
            throw new ApiException("手机号已存在");
        }

        // 校验身份证号是否存在
        if (checkIdCardExists(registerDTO.getIdCard())) {
            throw new ApiException("身份证号已存在");
        }

        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        user.setPassword(passwordUtils.encode(registerDTO.getPassword()));
        user.setStatus(1);
        user.setRole("USER");
        user.setRegisterIp(getClientIp());
        user.setRegisterTime(LocalDateTime.now());
        user.setIsVerified(0);
        userMapper.insert(user);
    }

    @Override
    public void updateAvatar(Long id, String avatarUrl) {
        // 查询用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ApiException("用户不存在");
        }

        // 更新头像
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
    }

    @Override
    public UserVO getCurrentUser() {
        // 从安全上下文中获取当前认证信息
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            // 获取用户ID
            UserDetails userDetails = (UserDetails) principal;
            // 假设用户ID存储在 UserDetails 的某个地方，这里根据项目实际情况获取
            // 如果你的 UserDetails 实现类 SecurityUser 包含了 User 对象，可以通过它获取ID
            if (userDetails instanceof com.medical.security.SecurityUser) {
                Long userId = ((com.medical.security.SecurityUser) userDetails).getUser().getId();
                 // 查询用户
                User user = userMapper.selectById(userId);
                if (user == null) {
                    throw new ApiException("用户不存在");
                }
                 // 转换VO
                return convertToVO(user);
            } else {
                // 如果不是 SecurityUser 类型，可能需要其他方式获取用户ID
                throw new BusinessException("无法获取当前用户信息");
            }
        }

        throw new BusinessException("用户未认证");
    }

    private boolean checkUsernameExists(String username) {
        return userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        ) > 0;
    }

    private boolean checkPhoneExists(String phone) {
        return userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getPhone, phone)
        ) > 0;
    }

    private boolean checkIdCardExists(String idCard) {
        return userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getIdCard, idCard)
        ) > 0;
    }

    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        // 确保realName字段与name字段保持一致
        vo.setRealName(user.getRealName());
        vo.setName(user.getRealName());
        return vo;
    }
} 