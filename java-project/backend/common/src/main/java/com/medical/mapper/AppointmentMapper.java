package com.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.model.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
    
    @Select("SELECT a.*, u.username as user_name, d.title as doctor_title, " +
            "u2.username as doctor_name, dp.name as department_name, " +
            "s.schedule_date, s.start_time, s.end_time " +
            "FROM appointment a " +
            "LEFT JOIN user u ON a.patient_id = u.id " +
            "LEFT JOIN doctor d ON a.doctor_id = d.id " +
            "LEFT JOIN user u2 ON d.user_id = u2.id " +
            "LEFT JOIN department dp ON d.department_id = dp.id " +
            "LEFT JOIN schedule s ON a.schedule_id = s.id " +
            "WHERE a.deleted = 0 AND a.patient_id = #{userId}")
    List<Appointment> selectByUserId(Long userId);
    
    @Select("SELECT a.*, u.username as user_name, d.title as doctor_title, " +
            "u2.username as doctor_name, dp.name as department_name, " +
            "s.schedule_date, s.start_time, s.end_time " +
            "FROM appointment a " +
            "LEFT JOIN user u ON a.patient_id = u.id " +
            "LEFT JOIN doctor d ON a.doctor_id = d.id " +
            "LEFT JOIN user u2 ON d.user_id = u2.id " +
            "LEFT JOIN department dp ON d.department_id = dp.id " +
            "LEFT JOIN schedule s ON a.schedule_id = s.id " +
            "WHERE a.deleted = 0 AND a.doctor_id = #{doctorId}")
    List<Appointment> selectByDoctorId(Long doctorId);
} 