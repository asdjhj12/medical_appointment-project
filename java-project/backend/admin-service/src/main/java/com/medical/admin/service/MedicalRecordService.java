package com.medical.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medical.model.dto.MedicalRecordDTO;
import com.medical.model.dto.TakeMedicineDTO;
import com.medical.model.entity.MedicalRecord;

import java.util.List;

public interface MedicalRecordService extends IService<MedicalRecord> {

    /**
     * 创建电子病历
     */
    void createMedicalRecord(MedicalRecordDTO medicalRecordDTO);

    /**
     * 根据ID获取电子病历详情
     */
    MedicalRecordDTO getMedicalRecordById(Long id);

    /**
     * 根据患者ID获取电子病历列表
     */
    List<MedicalRecordDTO> getMedicalRecordsByPatientId(Long patientId);

    /**
     * 根据医生ID获取电子病历列表
     */
    List<MedicalRecordDTO> getMedicalRecordsByDoctorId(Long doctorId);

    /**
     * 更新电子病历
     */
    void updateMedicalRecord(MedicalRecordDTO medicalRecordDTO);

    /**
     * 删除电子病历
     */
    void deleteMedicalRecord(Long id);

    /**
     * 用户取药
     */
    void takeMedicine(TakeMedicineDTO dto);

    /**
     * 获取所有电子病历
     */
    List<MedicalRecordDTO> getAllMedicalRecords();
} 