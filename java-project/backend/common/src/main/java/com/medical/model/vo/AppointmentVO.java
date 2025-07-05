package com.medical.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AppointmentVO {
    /**
     * 预约ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 排班ID
     */
    private Long scheduleId;

    /**
     * 预约编号
     */
    private String appointmentNumber;

    /**
     * 预约状态：0-待就诊，1-已完成，2-已取消
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 关联信息
     */
    private String userName;
    private String doctorName;
    private String departmentName;
    private LocalDate scheduleDate;
    private String periodText;
} 