package com.medical.doctor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medical.model.dto.DoctorDTO;
import com.medical.model.dto.DoctorQueryDTO;
import com.medical.model.dto.DoctorUpdateDTO;
import com.medical.model.entity.Doctor;
import com.medical.model.vo.DoctorVO;

import java.util.List;

public interface DoctorService extends IService<Doctor> {
    
    /**
     * 获取医生列表 (分页查询)
     */
    Page<DoctorVO> getDoctorList(DoctorQueryDTO queryDTO);

    /**
     * 获取医生详情 (VO对象)
     */
    DoctorVO getDoctorById(Long id);

    /**
     * 获取所有医生列表 (精简信息)
     */
    List<DoctorDTO> listAll();
    
    /**
     * 根据ID获取医生信息 (DTO对象)
     */
    DoctorDTO getById(Long id);
    
    /**
     * 根据科室ID获取医生列表 (精简信息)
     */
    List<DoctorDTO> listByDepartment(Long departmentId);
    
    /**
     * 创建医生
     */
    void createDoctor(DoctorUpdateDTO updateDTO);
    
    /**
     * 更新医生信息
     */
    void updateDoctor(Long id, DoctorUpdateDTO updateDTO);
    
    /**
     * 删除医生
     */
    void deleteDoctor(Long id);
    
    /**
     * 更新医生状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据用户ID获取医生信息
     */
    DoctorVO getDoctorByUserId(Long userId);

    /**
     * 获取在诊医生数量
     */
    Long getActiveDoctorsCount();
} 