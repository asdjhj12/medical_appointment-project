package com.medical.doctor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.common.ApiException;
import com.medical.mapper.DoctorMapper;
import com.medical.mapper.DoctorScheduleMapper;
import com.medical.doctor.service.DoctorScheduleService;
import com.medical.model.dto.DoctorScheduleDTO;
import com.medical.model.dto.ScheduleQueryDTO;
import com.medical.model.dto.ScheduleUpdateDTO;
import com.medical.model.entity.Doctor;
import com.medical.model.entity.DoctorSchedule;
import com.medical.model.vo.ScheduleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImpl extends ServiceImpl<DoctorScheduleMapper, DoctorSchedule> implements DoctorScheduleService {
    private final DoctorScheduleMapper scheduleMapper;
    private final DoctorMapper doctorMapper;

    @Override
    public Page<ScheduleVO> getScheduleList(ScheduleQueryDTO queryDTO) {
        Page<DoctorSchedule> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 使用新添加的方法进行联表查询
        Page<DoctorSchedule> schedulePage = scheduleMapper.selectSchedulePage(
            page, 
            queryDTO.getDoctorId(), 
            queryDTO.getScheduleDate(), 
            queryDTO.getPeriod(), 
            queryDTO.getStatus()
        );
        
        Page<ScheduleVO> voPage = new Page<>();
        BeanUtils.copyProperties(schedulePage, voPage, "records");
        voPage.setRecords(schedulePage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public ScheduleVO getScheduleById(Long id) {
        DoctorSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new ApiException("排班记录不存在");
        }
        return convertToVO(schedule);
    }

    @Override
    @Transactional
    public void addSchedule(ScheduleUpdateDTO updateDTO) {
        LambdaQueryWrapper<DoctorSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoctorSchedule::getDoctorId, updateDTO.getDoctorId())
                .eq(DoctorSchedule::getScheduleDate, updateDTO.getScheduleDate())
                .eq(DoctorSchedule::getPeriod, updateDTO.getPeriod());
        
        if (scheduleMapper.selectCount(wrapper) > 0) {
            throw new ApiException("该时间段已存在排班记录");
        }
        
        DoctorSchedule schedule = new DoctorSchedule();
        BeanUtils.copyProperties(updateDTO, schedule);
        schedule.setCurrentAppointments(0);
        schedule.setStatus(1);
        
        scheduleMapper.insert(schedule);
    }

    @Override
    @Transactional
    public void updateSchedule(Long id, ScheduleUpdateDTO updateDTO) {
        DoctorSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new ApiException("排班记录不存在");
        }
        
        LambdaQueryWrapper<DoctorSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoctorSchedule::getDoctorId, updateDTO.getDoctorId())
                .eq(DoctorSchedule::getScheduleDate, updateDTO.getScheduleDate())
                .eq(DoctorSchedule::getPeriod, updateDTO.getPeriod())
                .ne(DoctorSchedule::getId, id);
        
        if (scheduleMapper.selectCount(wrapper) > 0) {
            throw new ApiException("该时间段已存在排班记录");
        }
        
        BeanUtils.copyProperties(updateDTO, schedule);
        schedule.setUpdateTime(LocalDateTime.now());
        
        scheduleMapper.updateById(schedule);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        DoctorSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new ApiException("排班记录不存在");
        }
        
        if (schedule.getCurrentAppointments() > 0) {
            throw new ApiException("该排班已有预约，无法删除");
        }
        
        scheduleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateCurrentPatients(Long id, Integer count) {
        DoctorSchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new ApiException("排班记录不存在");
        }
        
        int newCount = schedule.getCurrentAppointments() + count;
        if (newCount < 0) {
            throw new ApiException("当前预约人数不能小于0");
        }
        if (newCount > schedule.getMaxAppointments()) {
            throw new ApiException("当前预约人数不能超过最大预约人数");
        }
        
        schedule.setCurrentAppointments(newCount);
        schedule.setUpdateTime(LocalDateTime.now());
        
        this.updateById(schedule);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        DoctorSchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new ApiException("排班记录不存在");
        }
        schedule.setStatus(status);
        this.updateById(schedule);
    }

    @Override
    @Transactional
    public void incrementCurrentAppointments(Long id) {
        updateCurrentPatients(id, 1);
    }

    @Override
    @Transactional
    public void decrementCurrentAppointments(Long id) {
        updateCurrentPatients(id, -1);
    }

    @Override
    public List<DoctorScheduleDTO> getScheduleByDoctor(Long doctorId, LocalDate scheduleDate) {
        LambdaQueryWrapper<DoctorSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoctorSchedule::getDoctorId, doctorId);
        wrapper.eq(DoctorSchedule::getScheduleDate, scheduleDate);
        List<DoctorSchedule> schedules = baseMapper.selectList(wrapper);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<DoctorScheduleDTO> getScheduleByDate(LocalDate scheduleDate) {
        LambdaQueryWrapper<DoctorSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoctorSchedule::getScheduleDate, scheduleDate);
        List<DoctorSchedule> schedules = baseMapper.selectList(wrapper);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void update(DoctorScheduleDTO doctorScheduleDTO) {
        DoctorSchedule schedule = this.getById(doctorScheduleDTO.getId());
        if (schedule == null) {
            throw new ApiException("排班记录不存在");
        }
        
        BeanUtils.copyProperties(doctorScheduleDTO, schedule);
        schedule.setUpdateTime(LocalDateTime.now());
        
        this.updateById(schedule);
    }

    @Override
    @Transactional
    public void create(DoctorScheduleDTO doctorScheduleDTO) {
        DoctorSchedule schedule = new DoctorSchedule();
        BeanUtils.copyProperties(doctorScheduleDTO, schedule);
        
        // 如果前端没有传递departmentId，则根据doctorId查询医生信息获取departmentId
        if (schedule.getDepartmentId() == null && schedule.getDoctorId() != null) {
            Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
            if (doctor != null && doctor.getDepartmentId() != null) {
                schedule.setDepartmentId(doctor.getDepartmentId());
            } else {
                throw new ApiException("无法获取医生所属科室，请确保医生信息完整");
            }
        }
        
        schedule.setCurrentAppointments(0);
        schedule.setStatus(1);
        schedule.setCreateTime(LocalDateTime.now());
        schedule.setUpdateTime(LocalDateTime.now());
        
        // 使用自定义的insertWithDepartmentId方法，确保包含department_id字段
        scheduleMapper.insertWithDepartmentId(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long id) {
        DoctorSchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new ApiException("排班记录不存在");
        }
        
        if (schedule.getCurrentAppointments() > 0) {
            throw new ApiException("该排班已有预约，无法删除");
        }
        
        this.removeById(id);
    }

    private ScheduleVO convertToVO(DoctorSchedule schedule) {
        ScheduleVO vo = new ScheduleVO();
        BeanUtils.copyProperties(schedule, vo);
        if (schedule.getPeriod() != null) {
            switch (schedule.getPeriod()) {
                case 1:
                    vo.setPeriodText("上午");
                    break;
                case 2:
                    vo.setPeriodText("下午");
                    break;
                case 3:
                    vo.setPeriodText("晚上");
                    break;
                default:
                    vo.setPeriodText("");
            }
        }
        
        // 使用从数据库查询获取的医生姓名和科室名称
        if (schedule.getDoctorName() != null) {
            vo.setDoctorName(schedule.getDoctorName());
        } else if (schedule.getDoctor() != null && schedule.getDoctor().getUser() != null) {
            vo.setDoctorName(schedule.getDoctor().getUser().getRealName());
        }
        
        if (schedule.getDepartmentName() != null) {
            vo.setDepartmentName(schedule.getDepartmentName());
        } else if (schedule.getDoctor() != null && schedule.getDoctor().getDepartment() != null) {
            vo.setDepartmentName(schedule.getDoctor().getDepartment().getName());
        }
        
        return vo;
    }

    private DoctorScheduleDTO convertToDTO(DoctorSchedule schedule) {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        BeanUtils.copyProperties(schedule, dto);
        if (schedule.getDoctor() != null) {
            if (schedule.getDoctor().getUser() != null) {
                 dto.setDoctorName(schedule.getDoctor().getUser().getRealName());
            }
            if (schedule.getDoctor().getDepartment() != null) {
                 dto.setDepartmentName(schedule.getDoctor().getDepartment().getName());
            }
        }
        return dto;
    }
} 