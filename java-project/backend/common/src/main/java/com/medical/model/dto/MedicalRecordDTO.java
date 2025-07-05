package com.medical.model.dto;

import com.medical.model.entity.MedicalRecordMedicine;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class MedicalRecordDTO {
    private Long id;

    @NotNull(message = "预约ID不能为空")
    private Long appointmentId;

    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    @NotNull(message = "诊断结果不能为空")
    @Size(max = 1000, message = "诊断结果长度不能超过1000个字符")
    private String diagnosis;

    @Size(max = 1000, message = "治疗方案长度不能超过1000个字符")
    private String treatmentPlan;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;

    // 非数据库字段，用于展示
    private String patientName;
    private String doctorName;
    private String departmentName;
    private String appointmentTime;

    private Integer status; // 0-未诊断 1-已诊断 2-已取药

    private String medicineInfo; // 用药明细，JSON字符串

    private String diagnosisTime; // 诊断时间
    private String takeTime; // 取药时间

    private String appointmentNumber; // 预约编号
    private Integer appointmentStatus; // 预约状态

    public Integer getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(Integer appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    /**
     * 就诊日期，格式：yyyy-MM-dd HH:mm:ss
     */
    private String visitTime;

    private String description; // 症状描述/主诉

    private List<MedicalRecordMedicine> medicines;
} 