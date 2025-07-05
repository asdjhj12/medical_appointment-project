package com.medical.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleQueryDTO {
    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 排班日期
     */
    private LocalDate scheduleDate;

    /**
     * 时段（1-上午，2-下午，3-晚上）
     */
    private Integer period;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;
} 