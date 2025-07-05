package com.medical.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medical.model.dto.AppointmentQueryDTO;
import com.medical.model.dto.AppointmentUpdateDTO;
import com.medical.model.entity.Appointment;
import com.medical.model.vo.AppointmentVO;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService extends IService<Appointment> {
    /**
     * 获取预约列表
     */
    Page<AppointmentVO> getAppointmentList(AppointmentQueryDTO queryDTO);

    /**
     * 获取预约详情
     */
    AppointmentVO getAppointmentById(Long id);

    /**
     * 创建预约
     */
    void createAppointment(AppointmentUpdateDTO updateDTO);

    /**
     * 更新预约状态
     */
    void updateAppointmentStatus(Long id, Integer status);

    /**
     * 取消预约
     */
    void cancelAppointment(Long id);

    /**
     * 完成预约
     */
    void completeAppointment(Long id);

    /**
     * 获取用户的预约列表
     */
    List<AppointmentVO> getByUserId(Long userId);
    
    /**
     * 获取医生的预约列表
     */
    List<AppointmentVO> getByDoctorId(Long doctorId);

    /**
     * 获取今日预约数量
     */
    Long getTodayAppointmentsCount();

    /**
     * 获取待诊患者数量
     */
    Long getWaitingPatientsCount();

    /**
     * 获取今日预约列表
     */
    List<AppointmentVO> getTodayAppointments(LocalDate date);
} 