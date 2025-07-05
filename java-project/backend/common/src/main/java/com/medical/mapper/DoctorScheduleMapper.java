package com.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.model.entity.DoctorSchedule;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DoctorScheduleMapper extends BaseMapper<DoctorSchedule> {
    
    @Select("SELECT s.*, d.title as doctor_title, d.specialty, u.real_name as doctor_name, " +
            "dp.name as department_name " +
            "FROM schedule s " +
            "LEFT JOIN doctor d ON s.doctor_id = d.id " +
            "LEFT JOIN user u ON d.user_id = u.id " +
            "LEFT JOIN department dp ON s.department_id = dp.id " +
            "WHERE s.deleted = 0 AND s.doctor_id = #{doctorId} AND s.schedule_date = #{scheduleDate}")
    List<DoctorSchedule> selectScheduleByDoctorAndDate(Long doctorId, LocalDate scheduleDate);
    
    @Select("SELECT s.*, d.title as doctor_title, d.specialty, u.real_name as doctor_name, " +
            "dp.name as department_name " +
            "FROM schedule s " +
            "LEFT JOIN doctor d ON s.doctor_id = d.id " +
            "LEFT JOIN user u ON d.user_id = u.id " +
            "LEFT JOIN department dp ON s.department_id = dp.id " +
            "WHERE s.deleted = 0 AND s.schedule_date = #{scheduleDate}")
    List<DoctorSchedule> selectScheduleByDate(LocalDate scheduleDate);
    
    @Select({"<script>", 
            "SELECT s.*, d.title as doctor_title, d.specialty, u.real_name as doctor_name, ",
            "dp.name as department_name ",
            "FROM schedule s ",
            "LEFT JOIN doctor d ON s.doctor_id = d.id ",
            "LEFT JOIN user u ON d.user_id = u.id ",
            "LEFT JOIN department dp ON s.department_id = dp.id ",
            "WHERE s.deleted = 0 ",
            "<if test='doctorId != null'>AND s.doctor_id = #{doctorId} </if>",
            "<if test='scheduleDate != null'>AND s.schedule_date = #{scheduleDate} </if>",
            "<if test='period != null'>AND s.period = #{period} </if>",
            "<if test='status != null'>AND s.status = #{status} </if>",
            "ORDER BY s.schedule_date ASC, s.period ASC",
            "</script>"})
    Page<DoctorSchedule> selectSchedulePage(Page<DoctorSchedule> page,
                                            @Param("doctorId") Long doctorId,
                                            @Param("scheduleDate") LocalDate scheduleDate,
                                            @Param("period") Integer period,
                                            @Param("status") Integer status);
    
    @Insert({
        "INSERT INTO schedule (doctor_id, department_id, schedule_date, period, max_appointments, current_appointments, status, create_time, update_time) ",
        "VALUES (#{doctorId}, #{departmentId}, #{scheduleDate}, #{period}, #{maxAppointments}, #{currentAppointments}, #{status}, #{createTime}, #{updateTime})"
    })
    int insertWithDepartmentId(DoctorSchedule schedule);
}
 