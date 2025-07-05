package com.medical.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MedicalRecordUpdateDTO {
    /**
     * 预约ID
     */
    @NotNull(message = "预约ID不能为空")
    private Long appointmentId;

    /**
     * 主诉
     */
    @NotBlank(message = "主诉不能为空")
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
    @NotBlank(message = "体格检查不能为空")
    private String physicalExam;

    /**
     * 辅助检查
     */
    private String auxiliaryExam;

    /**
     * 诊断结果
     */
    @NotBlank(message = "诊断结果不能为空")
    private String diagnosis;

    /**
     * 治疗方案
     */
    @NotBlank(message = "治疗方案不能为空")
    private String treatmentPlan;

    /**
     * 医嘱
     */
    @NotBlank(message = "医嘱不能为空")
    private String medicalAdvice;

    /**
     * 备注
     */
    private String remark;
} 