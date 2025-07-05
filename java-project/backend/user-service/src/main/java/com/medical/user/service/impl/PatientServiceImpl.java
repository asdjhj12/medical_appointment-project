package com.medical.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.common.ApiException;
import com.medical.mapper.PatientMapper;
import com.medical.mapper.UserMapper;
import com.medical.model.dto.PatientQueryDTO;
import com.medical.model.dto.PatientUpdateDTO;
import com.medical.model.entity.Patient;
import com.medical.model.entity.User;
import com.medical.model.vo.PatientVO;
import com.medical.user.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
    private final PatientMapper patientMapper;
    private final UserMapper userMapper;

    @Override
    public Page<PatientVO> getPatientList(PatientQueryDTO queryDTO) {
        Page<Patient> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getRealName()) || StringUtils.hasText(queryDTO.getPhone()) || StringUtils.hasText(queryDTO.getIdCard())) {
            wrapper.exists("SELECT 1 FROM sys_user u WHERE u.id = patient.user_id AND u.deleted = 0 AND ("
                    + (StringUtils.hasText(queryDTO.getRealName()) ? "u.real_name LIKE CONCAT('%', #{realName}, '%') OR " : "")
                    + (StringUtils.hasText(queryDTO.getPhone()) ? "u.phone LIKE CONCAT('%', #{phone}, '%') OR " : "")
                    + (StringUtils.hasText(queryDTO.getIdCard()) ? "patient.id_card LIKE CONCAT('%', #{idCard}, '%')" : "")
                    + ")");
        }
        
        Page<Patient> patientPage = patientMapper.selectPage(page, wrapper);
        Page<PatientVO> voPage = new Page<>();
        BeanUtils.copyProperties(patientPage, voPage, "records");
        voPage.setRecords(patientPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public PatientVO getPatientById(Long id) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null) {
            throw new ApiException("患者不存在");
        }
        return convertToVO(patient);
    }

    @Override
    public PatientVO getPatientByUserId(Long userId) {
        Patient patient = patientMapper.selectOne(
                new LambdaQueryWrapper<Patient>()
                        .eq(Patient::getUserId, userId)
        );
        if (patient == null) {
            throw new ApiException("患者不存在");
        }
        return convertToVO(patient);
    }

    @Override
    @Transactional
    public void updatePatient(Long id, PatientUpdateDTO updateDTO) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null) {
            throw new ApiException("患者不存在");
        }
        
        BeanUtils.copyProperties(updateDTO, patient);
        patientMapper.updateById(patient);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null) {
            throw new ApiException("患者不存在");
        }
        
        patientMapper.deleteById(id);
        userMapper.deleteById(patient.getUserId());
    }

    @Override
    @Transactional
    public void registerByUserId(Long userId) {
        // 检查是否已存在
        Patient exist = patientMapper.selectOne(
                new LambdaQueryWrapper<Patient>().eq(Patient::getUserId, userId)
        );
        if (exist != null) return;
        // 查用户
        User user = userMapper.selectById(userId);
        if (user == null) throw new ApiException("用户不存在");
        Patient patient = new Patient();
        patient.setUserId(user.getId());
        patient.setPatientName(user.getRealName());
        patient.setGender(user.getGender());
        patient.setAge(user.getAge());
        patient.setPhone(user.getPhone());
        patient.setIdCard(user.getIdCard());
        patient.setAddress(user.getAddress());
        patientMapper.insert(patient);
    }

    private PatientVO convertToVO(Patient patient) {
        PatientVO vo = new PatientVO();
        BeanUtils.copyProperties(patient, vo);
        
        User user = userMapper.selectById(patient.getUserId());
        if (user != null) {
            vo.setRealName(user.getRealName());
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
            vo.setGender(user.getGender());
            vo.setAge(user.getAge());
        }
        
        return vo;
    }
} 