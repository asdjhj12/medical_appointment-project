package com.medical.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medical.model.dto.PatientQueryDTO;
import com.medical.model.dto.PatientUpdateDTO;
import com.medical.model.entity.Patient;
import com.medical.model.vo.PatientVO;

public interface PatientService extends IService<Patient> {
    Page<PatientVO> getPatientList(PatientQueryDTO queryDTO);
    PatientVO getPatientById(Long id);
    PatientVO getPatientByUserId(Long userId);
    void updatePatient(Long id, PatientUpdateDTO updateDTO);
    void deletePatient(Long id);
    void registerByUserId(Long userId);
} 