package com.medical.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicalRecordVO {
    /**
     * 病历ID
     */
    private Long id;

    /**
     * 预约ID
     */
    private Long appointmentId;

    /**
     * 患者ID
     */
    private Long patientId;

    /**
     * 患者姓名
     */
    private String patientName;

    /**
     * 患者手机号
     */
    private String patientPhone;

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 科室ID
     */
    private Long departmentId;

    /**
     * 科室名称
     */
    private String departmentName;

    /**
     * 主诉
     */
    private String chiefComplaint;

    /**
     * 现病史
     */
    private String presentIllness;

    /**
     * 既往史
     */
    private String pastHistory;

    /**
     * 体格检查
     */
    private String physicalExam;

    /**
     * 诊断结果
     */
    private String diagnosis;

    /**
     * 治疗方案
     */
    private String treatment;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 症状描述/主诉
     */
    private String description;
} 