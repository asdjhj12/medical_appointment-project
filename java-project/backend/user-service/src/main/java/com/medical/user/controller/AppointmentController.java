package com.medical.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.user.service.AppointmentService;
import com.medical.common.api.Result;
import com.medical.model.dto.AppointmentQueryDTO;
import com.medical.model.dto.AppointmentUpdateDTO;
import com.medical.model.vo.AppointmentVO;
import com.medical.model.entity.Appointment;
import com.medical.model.entity.Doctor;
import com.medical.model.entity.Department;
import com.medical.model.entity.DoctorSchedule;
import com.medical.mapper.DoctorMapper;
import com.medical.mapper.DepartmentMapper;
import com.medical.mapper.DoctorScheduleMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "预约管理")
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private DoctorMapper doctorMapper;
    
    @Autowired
    private DepartmentMapper departmentMapper;
    
    @Autowired
    private DoctorScheduleMapper scheduleMapper;

    @ApiOperation("获取用户的预约列表")
    @GetMapping("/user/{userId}")
    public Result<List<AppointmentVO>> getByUserId(@PathVariable Long userId, HttpServletRequest request) {
        System.out.println("[AppointmentController] 收到请求: " + request.getRequestURI() + ", 参数 userId=" + userId);
        return Result.success(appointmentService.getByUserId(userId));
    }

    @ApiOperation("获取医生的预约列表")
    @GetMapping("/doctor/{doctorId}")
    public Result<List<AppointmentVO>> getByDoctorId(@PathVariable Long doctorId) {
        return Result.success(appointmentService.getByDoctorId(doctorId));
    }

    @ApiOperation("创建预约")
    @PostMapping
    public Result<Void> create(@Validated @RequestBody AppointmentUpdateDTO appointmentDTO) {
        System.out.println("进入预约Controller，参数: " + appointmentDTO);
        try {
            appointmentService.createAppointment(appointmentDTO);
            return Result.success();
        } catch (com.medical.common.ApiException e) {
            return Result.failed(e.getMessage());
        }
    }

    @GetMapping
    public Result<Page<AppointmentVO>> getAppointmentList(AppointmentQueryDTO queryDTO) {
        return Result.success(appointmentService.getAppointmentList(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public Result<AppointmentVO> getAppointmentById(@PathVariable Long id) {
        return Result.success(appointmentService.getAppointmentById(id));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public Result<Void> createAppointment(@Valid @RequestBody AppointmentUpdateDTO updateDTO) {
        appointmentService.createAppointment(updateDTO);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Result<Void> updateAppointmentStatus(@PathVariable Long id, @RequestParam Integer status) {
        appointmentService.updateAppointmentStatus(id, status);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public Result<Void> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return Result.success();
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Result<Void> completeAppointment(@PathVariable Long id) {
        appointmentService.completeAppointment(id);
        return Result.success();
    }

    @GetMapping("/today/list")
    public Result<List<AppointmentVO>> getTodayAppointments() {
        LocalDate today = LocalDate.now();
        List<AppointmentVO> appointments = appointmentService.getTodayAppointments(today);
        return Result.success(appointments);
    }

    @GetMapping("/count")
    public Result<Long> getAppointmentCount() {
        long count = appointmentService.count(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.medical.model.entity.Appointment>().eq("deleted", 0));
        return Result.success(count);
    }
    
    @ApiOperation("获取科室预约分布")
    @GetMapping("/stats/department-distribution")
    public Result<Map<String, Object>> getDepartmentDistribution() {
        System.out.println("开始获取科室预约分布数据...");
        
        try {
            // 查询所有有效的排班记录
            System.out.println("查询所有有效的排班记录...");
            List<DoctorSchedule> schedules = scheduleMapper.selectList(
                new LambdaQueryWrapper<DoctorSchedule>()
                    .eq(DoctorSchedule::getDeleted, 0)
                    .eq(DoctorSchedule::getStatus, 1) // 只查询状态正常的排班
            );
            
            System.out.println("查询到 " + schedules.size() + " 条排班记录");
            
            // 统计每个科室的预约数量，使用科室ID作为键
            Map<Long, Integer> departmentIdStats = new HashMap<>();
            
            // 遍历排班记录，统计科室预约数
            for (DoctorSchedule schedule : schedules) {
                Long departmentId = schedule.getDepartmentId();
                int currentAppointments = schedule.getCurrentAppointments();
                
                System.out.println("处理排班ID: " + schedule.getId() + 
                    ", 科室ID: " + departmentId + 
                    ", 医生ID: " + schedule.getDoctorId() + 
                    ", 当前预约数: " + currentAppointments);
                
                // 累加科室的预约数量
                departmentIdStats.put(departmentId, 
                    departmentIdStats.getOrDefault(departmentId, 0) + currentAppointments);
            }
            
            System.out.println("科室ID统计结果: " + departmentIdStats);
            
            // 获取所有科室信息并排序
            List<Department> allDepts = departmentMapper.selectList(
                new LambdaQueryWrapper<Department>()
                    .eq(Department::getDeleted, 0)
                    .eq(Department::getStatus, 1)
            );
            
            // 按照科室ID排序
            allDepts.sort(Comparator.comparing(Department::getId));
            
            // 构建返回数据
            List<String> departments = new ArrayList<>();
            List<Integer> counts = new ArrayList<>();
            
            for (Department dept : allDepts) {
                Long deptId = dept.getId();
                String deptName = dept.getName();
                int count = departmentIdStats.getOrDefault(deptId, 0);
                
                System.out.println("科室: " + deptName + ", 预约数: " + count);
                
                departments.add(deptName);
                counts.add(count);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("departments", departments);
            result.put("counts", counts);
            
            System.out.println("返回科室预约分布数据: " + result);
            return Result.success(result);
            
        } catch (Exception e) {
            System.out.println("获取科室预约分布数据时发生异常: " + e.getMessage());
            e.printStackTrace();
            
            // 发生异常时返回空数据
            Map<String, Object> defaultResult = new HashMap<>();
            defaultResult.put("departments", new ArrayList<>());
            defaultResult.put("counts", new ArrayList<>());
            
            return Result.success(defaultResult);
        }
    }
    
    @ApiOperation("获取预约趋势")
    @GetMapping("/stats/trend")
    public Result<Map<String, Object>> getAppointmentTrend(@RequestParam(defaultValue = "7") Integer days) {
        // 模拟数据
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        
        // 获取当前日期
        LocalDate today = LocalDate.now();
        
        // 生成过去7天的日期和预约数量
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.getMonthValue() + "/" + date.getDayOfMonth());
            
            // 模拟数据：根据日期生成一些预约数量
            int count = 0;
            if (i == 0) { // 今天
                count = 5;
            } else if (i == 1) { // 昨天
                count = 8;
            } else if (i == 2) { // 前天
                count = 12;
            } else if (i == 3) { // 大前天
                count = 7;
            } else {
                count = 3 + i; // 其他日期
            }
            
            counts.add(count);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("counts", counts);
        
        return Result.success(result);
    }
} 