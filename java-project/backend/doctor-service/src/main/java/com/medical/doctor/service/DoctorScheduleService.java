package com.medical.doctor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medical.model.dto.DoctorScheduleDTO;
import com.medical.model.dto.ScheduleQueryDTO;
import com.medical.model.dto.ScheduleUpdateDTO;
import com.medical.model.entity.DoctorSchedule;
import com.medical.model.vo.ScheduleVO;

import java.time.LocalDate;
import java.util.List;

public interface DoctorScheduleService extends IService<DoctorSchedule> {
    /**
     * 获取排班列表
     *
     * @param queryDTO 查询条件
     * @return 排班列表
     */
    Page<ScheduleVO> getScheduleList(ScheduleQueryDTO queryDTO);

    /**
     * 根据ID获取排班详情
     *
     * @param id 排班ID
     * @return 排班详情
     */
    ScheduleVO getScheduleById(Long id);

    /**
     * 新增排班
     *
     * @param updateDTO 排班信息
     */
    void addSchedule(ScheduleUpdateDTO updateDTO);

    /**
     * 更新排班
     *
     * @param id 排班ID
     * @param updateDTO 排班信息
     */
    void updateSchedule(Long id, ScheduleUpdateDTO updateDTO);

    /**
     * 删除排班
     *
     * @param id 排班ID
     */
    void deleteSchedule(Long id);

    /**
     * 更新当前预约人数
     *
     * @param id 排班ID
     * @param count 变更数量（正数增加，负数减少）
     */
    void updateCurrentPatients(Long id, Integer count);

    /**
     * 获取医生的排班列表
     */
    List<DoctorScheduleDTO> getScheduleByDoctor(Long doctorId, LocalDate scheduleDate);
    
    /**
     * 获取指定日期的所有排班
     */
    List<DoctorScheduleDTO> getScheduleByDate(LocalDate scheduleDate);
    
    /**
     * 创建排班
     */
    void create(DoctorScheduleDTO scheduleDTO);
    
    /**
     * 更新排班
     */
    void update(DoctorScheduleDTO scheduleDTO);
    
    /**
     * 删除排班
     */
    void delete(Long id);
    
    /**
     * 更新排班状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 增加当前预约数
     */
    void incrementCurrentAppointments(Long id);
    
    /**
     * 减少当前预约数
     */
    void decrementCurrentAppointments(Long id);
} 