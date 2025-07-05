package com.medical.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.common.ApiException;
import com.medical.mapper.*;
import com.medical.model.dto.AppointmentDTO;
import com.medical.model.dto.AppointmentQueryDTO;
import com.medical.model.dto.AppointmentUpdateDTO;
import com.medical.model.dto.MedicalRecordDTO;
import com.medical.model.entity.*;
import com.medical.model.vo.AppointmentVO;
import com.medical.user.feign.DoctorScheduleFeignClient;
import com.medical.user.feign.MedicalRecordFeignClient;
import com.medical.user.service.AppointmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("userAppointmentServiceImpl")
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {
    private final AppointmentMapper appointmentMapper;
    private final DoctorMapper doctorMapper;
    private final DoctorScheduleMapper scheduleMapper;
    private final PatientMapper patientMapper;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final MedicalRecordFeignClient medicalRecordFeignClient;
    private final DoctorScheduleFeignClient doctorScheduleFeignClient;

    public AppointmentServiceImpl(
            AppointmentMapper appointmentMapper,
            DoctorMapper doctorMapper,
            DoctorScheduleMapper scheduleMapper,
            PatientMapper patientMapper,
            UserMapper userMapper,
            DepartmentMapper departmentMapper,
            MedicalRecordFeignClient medicalRecordFeignClient,
            DoctorScheduleFeignClient doctorScheduleFeignClient) {
        this.appointmentMapper = appointmentMapper;
        this.doctorMapper = doctorMapper;
        this.scheduleMapper = scheduleMapper;
        this.patientMapper = patientMapper;
        this.userMapper = userMapper;
        this.departmentMapper = departmentMapper;
        this.medicalRecordFeignClient = medicalRecordFeignClient;
        this.doctorScheduleFeignClient = doctorScheduleFeignClient;
    }

    @Override
    public Page<AppointmentVO> getAppointmentList(AppointmentQueryDTO queryDTO) {
        Page<Appointment> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        
        if (queryDTO.getUserId() != null) {
            wrapper.eq(Appointment::getPatientId, queryDTO.getUserId());
        }
        
        if (queryDTO.getDoctorId() != null) {
            wrapper.eq(Appointment::getDoctorId, queryDTO.getDoctorId());
        }
        
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Appointment::getStatus, queryDTO.getStatus());
        }
        
        if (queryDTO.getAppointmentNumber() != null) {
            wrapper.eq(Appointment::getAppointmentNumber, queryDTO.getAppointmentNumber());
        }
        
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(Appointment::getCreateTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            wrapper.le(Appointment::getCreateTime, queryDTO.getEndTime());
        }
        
        wrapper.orderByDesc(Appointment::getCreateTime);
        
        Page<Appointment> appointmentPage = appointmentMapper.selectPage(page, wrapper);
        Page<AppointmentVO> voPage = new Page<>();
        BeanUtils.copyProperties(appointmentPage, voPage, "records");
        voPage.setRecords(appointmentPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public AppointmentVO getAppointmentById(Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new ApiException("预约记录不存在");
        }
        return convertToVO(appointment);
    }

    @Override
    @Transactional
    public void createAppointment(AppointmentUpdateDTO updateDTO) {
        // 检查排班是否存在
        DoctorSchedule schedule = scheduleMapper.selectById(updateDTO.getScheduleId());
        if (schedule == null) {
            throw new ApiException("排班不存在");
        }
        
        // 检查排班是否已满
        if (schedule.getCurrentAppointments() >= schedule.getMaxAppointments()) {
            throw new ApiException("该时段预约已满");
        }
        
        // 检查用户是否已经预约过该时段
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getPatientId, updateDTO.getPatientId())
               .eq(Appointment::getScheduleId, updateDTO.getScheduleId())
               .eq(Appointment::getDeleted, 0);
        
        Long count = appointmentMapper.selectCount(wrapper);
        if (count > 0) {
            throw new ApiException("您已预约过该时段");
        }
        
        // 创建预约
        Appointment appointment = new Appointment();
        appointment.setPatientId(updateDTO.getPatientId());
        appointment.setDoctorId(schedule.getDoctorId());
        appointment.setScheduleId(updateDTO.getScheduleId());
        appointment.setAppointmentNumber(generateAppointmentNumber());
        appointment.setStatus(0); // 待处理状态
        appointment.setDescription(updateDTO.getDescription());
        
        appointmentMapper.insert(appointment);
        System.out.println("插入预约成功，预约ID: " + appointment.getId() + ", 患者ID: " + appointment.getPatientId() + ", 医生ID: " + appointment.getDoctorId());
        
        // 创建病历记录，使用最多3次重试
        for (int retry = 0; retry < 3; retry++) {
            try {
                // 创建病历记录
                MedicalRecordDTO record = new MedicalRecordDTO();
                record.setAppointmentId(appointment.getId());
                
                // 确保使用正确的患者ID
                // 注意：此处的patientId应该是患者表的ID而不是用户表的ID
                Long patientId = appointment.getPatientId();
                System.out.println("病历记录使用的患者ID: " + patientId);
                
                record.setPatientId(patientId);
                record.setDoctorId(appointment.getDoctorId());
                record.setDiagnosis(""); // 初始为空
                record.setTreatmentPlan(""); // 初始为空
                record.setRemarks(""); // 初始为空
                
                // 如果有症状描述，则添加到病历记录中
                if (appointment.getDescription() != null && !appointment.getDescription().isEmpty()) {
                    record.setDescription(appointment.getDescription());
                }
                
                System.out.println("准备创建病历记录 (尝试 " + (retry + 1) + "/3): " + record);
                
                try {
                    // 尝试通过Feign客户端调用admin-service
                    medicalRecordFeignClient.createMedicalRecord(record);
                    System.out.println("病历记录创建成功");
                    break; // 成功创建，跳出重试循环
                } catch (Exception feign) {
                    System.out.println("Feign调用失败: " + feign.getMessage());
                    
                    // 如果是最后一次重试且启用了备用HTTP调用，则尝试直接HTTP调用
                    if (retry == 2) {
                        try {
                            System.out.println("尝试直接HTTP调用创建病历...");
                            // 这里可以添加直接的HTTP调用实现，如使用RestTemplate
                        } catch (Exception http) {
                            System.out.println("直接HTTP调用也失败: " + http.getMessage());
                            throw http; // 重新抛出异常继续处理
                        }
                    } else {
                        throw feign; // 重新抛出异常继续处理
                    }
                }
            } catch (Exception e) {
                System.out.println("创建病历记录失败 (尝试 " + (retry + 1) + "/3): " + e.getMessage());
                e.printStackTrace();
                
                if (retry == 2) { // 最后一次尝试
                    System.out.println("病历记录创建失败，已达到最大重试次数。预约ID: " + appointment.getId());
                    // 不抛出异常，允许预约流程继续
                } else {
                    try {
                        // 等待一段时间后重试
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        // 更新排班当前预约人数
        try {
            doctorScheduleFeignClient.incrementCurrentAppointments(updateDTO.getScheduleId());
        } catch (Exception e) {
            // 记录错误但不影响预约流程
            System.out.println("更新排班人数失败，排班ID: " + updateDTO.getScheduleId() + ", 错误: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateAppointmentStatus(Long id, Integer status) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new ApiException("预约记录不存在");
        }
        
        if (appointment.getStatus().equals(status)) {
            throw new ApiException("预约状态未发生变化");
        }
        
        appointment.setStatus(status);
        appointmentMapper.updateById(appointment);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new ApiException("预约记录不存在");
        }
        
        if (appointment.getStatus() != 0) {
            throw new ApiException("只能取消待就诊的预约");
        }
        
        appointment.setStatus(3);
        appointmentMapper.updateById(appointment);
        
        // 更新排班当前预约人数
        doctorScheduleFeignClient.decrementCurrentAppointments(appointment.getScheduleId());
    }

    @Override
    @Transactional
    public void completeAppointment(Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new ApiException("预约记录不存在");
        }
        
        if (appointment.getStatus() != 0) {
            throw new ApiException("只能完成待就诊的预约");
        }
        
        appointment.setStatus(1);
        appointmentMapper.updateById(appointment);
    }

    @Override
    public Long getTodayAppointmentsCount() {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        LocalDate today = LocalDate.now();
        
        // 关联schedule表，根据schedule_date字段过滤
        wrapper.eq(Appointment::getDeleted, 0)
               .exists("SELECT 1 FROM schedule s WHERE s.id = appointment.schedule_id AND s.schedule_date = {0}", today);
        
        return appointmentMapper.selectCount(wrapper);
    }

    @Override
    public Long getWaitingPatientsCount() {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        // 假设待诊状态是 PENDING
        wrapper.eq(Appointment::getStatus, 0) // 假设 PENDING 状态对应数据库中的 0
               .eq(Appointment::getDeleted, 0); // 确保只统计未删除的
        return appointmentMapper.selectCount(wrapper);
    }

    @Override
    public List<AppointmentVO> getTodayAppointments(LocalDate date) {
        // 构建查询条件
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getDeleted, 0)
               .exists("SELECT 1 FROM schedule s WHERE s.id = appointment.schedule_id AND s.schedule_date = {0}", date)
               .orderByDesc(Appointment::getCreateTime);

        // 查询预约记录
        List<Appointment> appointments = appointmentMapper.selectList(wrapper);
        
        // 转换为VO对象
        return appointments.stream().map(appointment -> {
            AppointmentVO vo = new AppointmentVO();
            BeanUtils.copyProperties(appointment, vo);
            
            // 获取关联信息
            DoctorSchedule schedule = scheduleMapper.selectById(appointment.getScheduleId());
            if (schedule != null) {
                vo.setScheduleDate(schedule.getScheduleDate()); // 直接设置排班日期
                vo.setPeriodText(getTimeSlotText(schedule.getPeriod())); // 设置时段文本
                
                // 获取医生信息
                Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
                if (doctor != null) {
                    // 获取医生用户真实姓名
                    User doctorUser = userMapper.selectById(doctor.getUserId());
                    if (doctorUser != null) {
                        vo.setDoctorName(doctorUser.getRealName());
                    }
                    
                    // 获取科室信息
                    Department department = departmentMapper.selectById(doctor.getDepartmentId());
                    if (department != null) {
                        vo.setDepartmentName(department.getName());
                    }
                }
            }
            
            // 获取患者信息 (直接通过appointment中的userId关联user表获取真实姓名)
            User patientUser = userMapper.selectById(appointment.getPatientId());
            if (patientUser != null) {
                vo.setUserName(patientUser.getRealName());
            }
            
            return vo;
        }).collect(Collectors.toList());
    }

    private String getTimeSlotText(Integer period) {
        switch (period) {
            case 1:
                return "上午";
            case 2:
                return "下午";
            case 3:
                return "晚上";
            default:
                return "未知";
        }
    }

    private AppointmentVO convertToVO(Appointment appointment) {
        AppointmentVO vo = new AppointmentVO();
        BeanUtils.copyProperties(appointment, vo);
        
        // 添加调试日志
        System.out.println("正在转换预约数据，预约ID: " + appointment.getId() + ", 患者ID: " + appointment.getPatientId() + ", 医生ID: " + appointment.getDoctorId());
        
        // 获取医生信息
        Doctor doctor = doctorMapper.selectById(appointment.getDoctorId());
        if (doctor != null) {
            // 直接从doctor表获取医生姓名
            vo.setDoctorName(doctor.getDoctorName());
            System.out.println("从doctor表获取到的医生姓名: " + doctor.getDoctorName());
            
            // 获取科室信息
            if (doctor.getDepartmentId() != null) {
                Department department = departmentMapper.selectById(doctor.getDepartmentId());
                if (department != null) {
                    vo.setDepartmentName(department.getName());
                    System.out.println("从department表获取到的科室名称: " + department.getName());
                } else {
                    vo.setDepartmentName("未知科室");
                    System.out.println("未找到对应的科室信息");
                }
            }
        } else {
            vo.setDoctorName("未知医生");
            System.out.println("未找到对应的医生信息");
        }
        
        // 获取患者信息
        System.out.println("患者ID: " + appointment.getPatientId());
        
        // 直接从patient表查询患者信息
        Patient patient = patientMapper.selectById(appointment.getPatientId());
        if (patient != null) {
            // 直接使用patient表中的patient_name字段
            vo.setUserName(patient.getPatientName());
            vo.setUserId(patient.getUserId());
            System.out.println("从patient表获取患者姓名: " + patient.getPatientName());
        } else {
            // 兼容旧数据，尝试从user表查询
            User patientUser = userMapper.selectById(appointment.getPatientId());
            if (patientUser != null) {
                System.out.println("从user表获取患者姓名: " + patientUser.getRealName());
                vo.setUserName(patientUser.getRealName());
                vo.setUserId(patientUser.getId());
            } else {
                System.out.println("在patient表和user表中均未找到患者信息");
                vo.setUserName("未知患者");
            }
        }
        
        // 获取排班信息
        DoctorSchedule schedule = scheduleMapper.selectById(appointment.getScheduleId());
        if (schedule != null) {
            vo.setScheduleDate(schedule.getScheduleDate());
            vo.setPeriodText(getTimeSlotText(schedule.getPeriod()));
            System.out.println("获取到排班日期: " + schedule.getScheduleDate() + ", 时段: " + getTimeSlotText(schedule.getPeriod()));
        } else {
            System.out.println("未找到对应的排班信息");
        }
        
        return vo;
    }

    @Override
    public List<AppointmentVO> getByUserId(Long userId) {
        // 1. 先查 patientId
        Patient patient = patientMapper.selectOne(
                new LambdaQueryWrapper<Patient>()
                        .eq(Patient::getUserId, userId)
                        .eq(Patient::getDeleted, 0)
        );
        if (patient == null) {
            // 没有患者信息，返回空列表
            return List.of();
        }
        // 2. 再查 appointment
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getPatientId, patient.getId());
        List<Appointment> appointments = baseMapper.selectList(wrapper);
        return appointments.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentVO> getByDoctorId(Long doctorId) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getDoctorId, doctorId);
        List<Appointment> appointments = baseMapper.selectList(wrapper);
        return appointments.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        BeanUtils.copyProperties(appointment, dto);
        
        if (appointment.getUser() != null) {
            dto.setUserName(appointment.getUser().getUsername());
        }
        
        if (appointment.getDoctor() != null) {
            dto.setDoctorName(appointment.getDoctor().getUser().getUsername());
            dto.setDepartmentName(appointment.getDoctor().getDepartment().getName());
        }
        
        if (appointment.getSchedule() != null) {
            dto.setScheduleDate(appointment.getSchedule().getScheduleDate().toString());
            dto.setScheduleTime(getTimeSlotText(appointment.getSchedule().getPeriod())); // 根据 period 设置时段文本
        }
        
        return dto;
    }

    private String generateAppointmentNumber() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuidSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return datePrefix + uuidSuffix;
    }
} 