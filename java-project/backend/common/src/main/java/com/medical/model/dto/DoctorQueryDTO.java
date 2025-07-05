package com.medical.model.dto;

import lombok.Data;

@Data
public class DoctorQueryDTO {
    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 科室
     */
    private Long departmentId;

    /**
     * 职称
     */
    private String title;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;
} 