package com.medical.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("medical_record")
public class MedicalRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long appointmentId;

    private Long patientId;

    private Long doctorId;

    private String diagnosis;

    private String treatmentPlan;

    private String remarks;

    private Integer status; // 病历状态（0-未处理，1-已诊断，2-已取药）
    private String medicineInfo; // 用药明细（JSON）

    private String description; // 症状描述/主诉

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private Appointment appointment;

    @TableField(exist = false)
    private User patient;

    @TableField(exist = false)
    private Doctor doctor;

    @TableField("visit_time")
    private LocalDateTime visitTime;
} 