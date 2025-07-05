package com.medical.doctor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.common.exception.BusinessException;
import com.medical.mapper.DepartmentMapper;
import com.medical.mapper.DoctorMapper;
import com.medical.mapper.UserMapper;
import com.medical.model.dto.DoctorDTO;
import com.medical.model.dto.DoctorQueryDTO;
import com.medical.model.dto.DoctorUpdateDTO;
import com.medical.model.entity.Department;
import com.medical.model.entity.Doctor;
import com.medical.model.entity.User;
import com.medical.model.vo.DoctorVO;
import com.medical.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

    private final DoctorMapper doctorMapper;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;

    @Override
    public Page<DoctorVO> getDoctorList(DoctorQueryDTO queryDTO) {
        Page<Doctor> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(queryDTO.getRealName())) {
             LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
             userWrapper.eq(User::getRealName, queryDTO.getRealName());
             User user = userMapper.selectOne(userWrapper);
             if (user != null) {
                 wrapper.eq(Doctor::getUserId, user.getId());
             } else {
                 return new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
             }
        }
        wrapper.eq(queryDTO.getDepartmentId() != null, Doctor::getDepartmentId, queryDTO.getDepartmentId());
        wrapper.eq(StringUtils.isNotBlank(queryDTO.getTitle()), Doctor::getTitle, queryDTO.getTitle());
        wrapper.eq(queryDTO.getStatus() != null, Doctor::getStatus, queryDTO.getStatus());
        wrapper.orderByDesc(Doctor::getCreateTime);
        
        Page<Doctor> doctorPage = doctorMapper.selectPage(page, wrapper);
        
        Page<DoctorVO> voPage = new Page<>();
        BeanUtils.copyProperties(doctorPage, voPage, "records");
        
        List<DoctorVO> voList = doctorPage.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public DoctorVO getDoctorById(Long id) {
        Doctor doctor = doctorMapper.selectById(id);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }
        
        // 获取完整的医生信息，包括用户名和密码
        DoctorVO doctorVO = convertToVO(doctor);
        
        // 从User表获取用户名和密码
        if (doctor.getUserId() != null) {
            User user = userMapper.selectById(doctor.getUserId());
            if (user != null) {
                doctorVO.setUsername(user.getUsername());
                doctorVO.setPassword(user.getPassword());
            }
        }
        
        return doctorVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDoctor(Long id, DoctorUpdateDTO updateDTO) {
        Doctor doctor = doctorMapper.selectById(id);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        User user = userMapper.selectById(doctor.getUserId());
        if (user != null) {
            // 复制基本属性
            BeanUtils.copyProperties(updateDTO, user, "id");
            
            // 处理用户名和密码
            if (StringUtils.isNotBlank(updateDTO.getUsername())) {
                user.setUsername(updateDTO.getUsername());
            }
            
            if (StringUtils.isNotBlank(updateDTO.getPassword())) {
                user.setPassword(updateDTO.getPassword());
            }
            
            userMapper.updateById(user);
        }

        BeanUtils.copyProperties(updateDTO, doctor, "id", "userId");
        doctorMapper.updateById(doctor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorMapper.selectById(id);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        userMapper.deleteById(doctor.getUserId());
        doctorMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Doctor doctor = doctorMapper.selectById(id);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        User user = userMapper.selectById(doctor.getUserId());
        if (user != null) {
            user.setStatus(status);
            userMapper.updateById(user);
        }

        doctor.setStatus(status);
        doctorMapper.updateById(doctor);
    }

    @Override
    public List<DoctorDTO> listAll() {
        List<Doctor> doctors = this.list();
        return doctors.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public DoctorDTO getById(Long id) {
        Doctor doctor = super.getById(id);
        if (doctor == null) {
            return null;
        }
        return convertToDTO(doctor);
    }

    @Override
    public List<DoctorDTO> listByDepartment(Long departmentId) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getDepartmentId, departmentId);
        List<Doctor> doctors = this.list(wrapper);
        return doctors.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDoctor(DoctorUpdateDTO updateDTO) {
        System.out.println("创建医生，医生姓名: " + updateDTO.getDoctorName() + ", 科室ID: " + updateDTO.getDepartmentId());
        
        // 创建或关联用户账号
        User user = new User();
        BeanUtils.copyProperties(updateDTO, user);
        
        // 设置用户名和密码
        if (StringUtils.isNotBlank(updateDTO.getUsername())) {
            user.setUsername(updateDTO.getUsername());
        }
        
        if (StringUtils.isNotBlank(updateDTO.getPassword())) {
            user.setPassword(updateDTO.getPassword());
        }
        
        user.setRole("DOCTOR");
        user.setStatus(1);
        // 如果没有设置真实姓名，则使用医生姓名
        if (StringUtils.isBlank(user.getRealName()) && StringUtils.isNotBlank(updateDTO.getDoctorName())) {
            user.setRealName(updateDTO.getDoctorName());
        }
        userMapper.insert(user);
        System.out.println("创建用户成功，用户ID: " + user.getId());

        // 创建医生记录
        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(updateDTO, doctor);
        
        // 确保设置医生姓名
        doctor.setDoctorName(updateDTO.getDoctorName());
        
        // 关联用户ID
        doctor.setUserId(user.getId());
        
        // 设置科室ID
        if (updateDTO.getDepartmentId() != null) {
            doctor.setDepartmentId(updateDTO.getDepartmentId());
        } else if (StringUtils.isNotBlank(updateDTO.getDepartment())) {
            // 兼容旧代码：根据科室名称查找科室ID
            Department department = departmentMapper.selectOne(
                new LambdaQueryWrapper<Department>()
                    .eq(Department::getName, updateDTO.getDepartment())
                    .last("LIMIT 1")
            );
            if (department != null) {
                doctor.setDepartmentId(department.getId());
            } else {
                throw new BusinessException("科室不存在: " + updateDTO.getDepartment());
            }
        }
        
        doctorMapper.insert(doctor);
        System.out.println("创建医生成功，医生ID: " + doctor.getId());
    }

    @Override
    public DoctorVO getDoctorByUserId(Long userId) {
        Doctor doctor = doctorMapper.selectOne(
                new LambdaQueryWrapper<Doctor>()
                        .eq(Doctor::getUserId, userId)
        );
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }
        return convertToVO(doctor);
    }

    @Override
    public Long getActiveDoctorsCount() {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getStatus, 1)
               .eq(Doctor::getDeleted, 0);
        return doctorMapper.selectCount(wrapper);
    }

    private DoctorVO convertToVO(Doctor doctor) {
        DoctorVO vo = new DoctorVO();
        BeanUtils.copyProperties(doctor, vo);

        System.out.println("医生数据转换，医生ID: " + doctor.getId() + ", 医生姓名: " + doctor.getDoctorName());

        // 优先使用医生表中的doctorName字段
        if (doctor.getDoctorName() != null && !doctor.getDoctorName().isEmpty()) {
            vo.setRealName(doctor.getDoctorName());
        }
        
        // 从user表获取用户信息
        User user = userMapper.selectById(doctor.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setPassword(user.getPassword());
            vo.setRealName(StringUtils.isNotBlank(vo.getRealName()) ? vo.getRealName() : user.getRealName());
            vo.setGender(user.getGender());
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
            vo.setAvatar(user.getAvatar());
            if (user.getBirthDate() != null) {
                vo.setAge(Period.between(user.getBirthDate(), LocalDate.now()).getYears());
            }
        }

        Department department = departmentMapper.selectById(doctor.getDepartmentId());
        if (department != null) {
            vo.setDepartmentName(department.getName());
            System.out.println("获取到科室信息，科室ID: " + department.getId() + ", 科室名称: " + department.getName());
        } else {
            System.out.println("未找到科室信息，科室ID: " + doctor.getDepartmentId());
        }

        return vo;
    }

    private DoctorDTO convertToDTO(Doctor doctor) {
         DoctorDTO dto = new DoctorDTO();
         dto.setId(doctor.getId());
         dto.setUserId(doctor.getUserId());
         dto.setDepartmentId(doctor.getDepartmentId());
         dto.setTitle(doctor.getTitle());
         dto.setSpecialty(doctor.getSpecialty());
         dto.setIntroduction(doctor.getIntroduction());
         dto.setStatus(doctor.getStatus());

        System.out.println("转换医生数据为DTO，医生ID: " + doctor.getId() + ", 医生姓名: " + doctor.getDoctorName());

        // 优先使用医生表中的doctorName字段
        if (doctor.getDoctorName() != null && !doctor.getDoctorName().isEmpty()) {
            dto.setUserName(doctor.getDoctorName());
            System.out.println("使用医生表中的doctorName: " + doctor.getDoctorName());
        } else if (doctor.getUserId() != null) {
            // 兼容旧数据，从user表获取
              User user = userMapper.selectById(doctor.getUserId());
              if (user != null) {
                  dto.setUserName(user.getRealName());
                System.out.println("从user表获取医生姓名: " + user.getRealName());
              }
         }

         if (doctor.getDepartmentId() != null) {
             Department department = departmentMapper.selectById(doctor.getDepartmentId());
             if (department != null) {
                 dto.setDepartmentName(department.getName());
                System.out.println("获取到科室名称: " + department.getName());
            } else {
                System.out.println("未找到科室信息，科室ID: " + doctor.getDepartmentId());
             }
         }

         return dto;
    }
} 