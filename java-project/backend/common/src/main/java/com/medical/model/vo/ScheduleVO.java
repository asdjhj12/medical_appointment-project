package com.medical.model.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleVO {
    /**
     * 排班ID
     */
    private Long id;

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 科室
     */
    private String departmentName;

    /**
     * 排班日期
     */
    private LocalDate scheduleDate;

    /**
     * 时段（1-上午，2-下午，3-晚上）
     */
    private Integer period;

    /**
     * 最大接诊人数
     */
    private Integer maxAppointments;

    /**
     * 当前预约人数
     */
    private Integer currentAppointments;

    /**
     * 状态（0-未开始，1-进行中，2-已结束）
     */
    private Integer status;

    /**
     * 时段文本描述
     */
    private String periodText;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;
} 