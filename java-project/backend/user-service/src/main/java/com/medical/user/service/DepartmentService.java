package com.medical.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medical.model.dto.DepartmentDTO;
import com.medical.model.entity.Department;

import java.util.List;

public interface DepartmentService extends IService<Department> {
    
    /**
     * 获取所有科室列表
     */
    List<DepartmentDTO> listAll();
    
    /**
     * 根据ID获取科室信息
     */
    DepartmentDTO getById(Long id);
    
    /**
     * 创建科室
     */
    void create(DepartmentDTO departmentDTO);
    
    /**
     * 更新科室信息
     */
    void update(DepartmentDTO departmentDTO);
    
    /**
     * 删除科室
     */
    void delete(Long id);
    
    /**
     * 更新科室状态
     */
    void updateStatus(Long id, Integer status);
} 