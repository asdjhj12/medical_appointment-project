package com.medical.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AppointmentUpdateDTO {
    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    /**
     * 医生ID
     */
    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    /**
     * 排班ID
     */
    @NotNull(message = "排班ID不能为空")
    private Long scheduleId;

    /**
     * 备注或描述
     */
    private String description;
} 