package com.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.model.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
    
    @Select("SELECT d.*, u.username as user_name, dp.name as department_name " +
            "FROM doctor d " +
            "LEFT JOIN user u ON d.user_id = u.id " +
            "LEFT JOIN department dp ON d.department_id = dp.id " +
            "WHERE d.deleted = 0")
    List<Doctor> selectDoctorList();
    
    @Select("SELECT d.*, u.username as user_name, dp.name as department_name " +
            "FROM doctor d " +
            "LEFT JOIN user u ON d.user_id = u.id " +
            "LEFT JOIN department dp ON d.department_id = dp.id " +
            "WHERE d.deleted = 0 AND d.department_id = #{departmentId}")
    List<Doctor> selectDoctorListByDepartment(Long departmentId);
} 