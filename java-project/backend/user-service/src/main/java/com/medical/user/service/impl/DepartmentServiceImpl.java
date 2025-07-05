package com.medical.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.user.service.DepartmentService;
import com.medical.mapper.DepartmentMapper;
import com.medical.model.dto.DepartmentDTO;
import com.medical.model.entity.Department;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    public List<DepartmentDTO> listAll() {
        List<Department> departments = this.list();
        return departments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public DepartmentDTO getById(Long id) {
        Department department = super.getById(id);
        return department != null ? convertToDTO(department) : null;
    }

    @Override
    @Transactional
    public void create(DepartmentDTO departmentDTO) {
        Department department = new Department();
        BeanUtils.copyProperties(departmentDTO, department);
        this.save(department);
    }

    @Override
    @Transactional
    public void update(DepartmentDTO departmentDTO) {
        Department department = super.getById(departmentDTO.getId());
        if (department != null) {
            BeanUtils.copyProperties(departmentDTO, department);
            this.updateById(department);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Department department = new Department();
        department.setId(id);
        department.setStatus(status);
        this.updateById(department);
    }

    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        BeanUtils.copyProperties(department, dto);
        return dto;
    }
} 