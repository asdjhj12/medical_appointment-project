package com.medical.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.admin.service.MedicalRecordService;
import com.medical.common.exception.BusinessException;
import com.medical.doctor.service.DoctorService;
import com.medical.mapper.*;
import com.medical.medicine.service.MedicineInventoryService;
import com.medical.medicine.service.MedicineStockRecordService;
import com.medical.model.dto.*;
import com.medical.model.entity.*;
import com.medical.model.vo.DoctorVO;
import com.medical.model.vo.MedicalRecordVO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl extends ServiceImpl<MedicalRecordMapper, MedicalRecord> implements MedicalRecordService {
    private final MedicalRecordMapper medicalRecordMapper;
    private final PatientMapper patientMapper;
    private final DoctorMapper doctorMapper;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final AppointmentMapper appointmentMapper;
    @Autowired
    private MedicineInventoryService medicineInventoryService;
    @Autowired
    private MedicineStockRecordService medicineStockRecordService;
    @Autowired
    private MedicalRecordMedicineMapper medicalRecordMedicineMapper;

    @Override
    public MedicalRecordDTO getMedicalRecordById(Long id) {
        MedicalRecord record = medicalRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("电子病历记录不存在");
        }
        MedicalRecordDTO dto = convertToDTO(record);
        dto.setAppointmentStatus(appointmentMapper.selectById(record.getAppointmentId()).getStatus());
        System.out.println("赋值后dto.appointmentStatus=" + dto.getAppointmentStatus());

        // 查询用药明细
        List<MedicalRecordMedicine> medicines = medicalRecordMedicineMapper.selectByMedicalRecordId(id);
        System.out.println("【DEBUG】查到的用药明细：" + medicines);

        dto.setMedicines(medicines);

        return dto;
    }

    @Override
    @Transactional
    public void createMedicalRecord(MedicalRecordDTO medicalRecordDTO) {
        MedicalRecord medicalRecord = new MedicalRecord();
        BeanUtils.copyProperties(medicalRecordDTO, medicalRecord);
        
        try {
            // 检查预约是否存在
            Appointment appointment = appointmentMapper.selectById(medicalRecordDTO.getAppointmentId());
            
            if (appointment != null) {
                System.out.println("【创建病历】找到预约信息: ID=" + appointment.getId() 
                    + ", 患者ID=" + appointment.getPatientId() 
                    + ", 医生ID=" + appointment.getDoctorId());
                
                // 检查是否已存在病历
                LambdaQueryWrapper<MedicalRecord> existWrapper = new LambdaQueryWrapper<>();
                existWrapper.eq(MedicalRecord::getAppointmentId, medicalRecordDTO.getAppointmentId());
                if (medicalRecordMapper.selectCount(existWrapper) > 0) {
                    throw new BusinessException("该预约已存在病历记录");
                }
                
                // 自动带入预约表的 description 字段
                if (appointment.getDescription() != null) {
                    medicalRecord.setDescription(appointment.getDescription());
                }
                
                // 确保使用预约表中的患者ID和医生ID
                medicalRecord.setPatientId(appointment.getPatientId());
                medicalRecord.setDoctorId(appointment.getDoctorId());
            } else {
                System.out.println("【创建病历】未找到预约信息ID=" + medicalRecordDTO.getAppointmentId() 
                    + "，将使用传入的数据创建病历");
                // 直接使用传入的数据
            }
        } catch (Exception e) {
            System.out.println("【创建病历】检查预约信息时出错: " + e.getMessage());
        }
        
        // 初始状态
        medicalRecord.setStatus(0);
        
        System.out.println("【创建病历】准备保存病历记录: " + medicalRecord);
        
        try {
            this.save(medicalRecord);
            System.out.println("【创建病历】病历记录保存成功，ID: " + medicalRecord.getId());
        } catch (Exception e) {
            System.out.println("【创建病历】病历记录保存失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<MedicalRecordDTO> getMedicalRecordsByPatientId(Long patientId) {
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalRecord::getPatientId, patientId);
        List<MedicalRecord> medicalRecords = this.list(wrapper);
        return medicalRecords.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecordDTO> getMedicalRecordsByDoctorId(Long doctorId) {
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalRecord::getDoctorId, doctorId);
        List<MedicalRecord> medicalRecords = this.list(wrapper);
        return medicalRecords.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateMedicalRecord(MedicalRecordDTO medicalRecordDTO) {
        MedicalRecord medicalRecord = this.getById(medicalRecordDTO.getId());
        if (medicalRecord == null) {
            throw new BusinessException("电子病历记录不存在");
        }
        Appointment appointment = appointmentMapper.selectById(medicalRecord.getAppointmentId());
        if (appointment != null && appointment.getStatus() != 2) {
            throw new BusinessException("关联的预约未完成，无法修改病历");
        }
        BeanUtils.copyProperties(medicalRecordDTO, medicalRecord, "id", "appointmentId", "patientId", "doctorId");
        // visitTime 字段类型转换
        if (medicalRecordDTO.getVisitTime() != null) {
            try {
                medicalRecord.setVisitTime(java.time.LocalDateTime.parse(medicalRecordDTO.getVisitTime().replace(" ", "T")));
            } catch (Exception e) {
                // 忽略格式错误
            }
        }
        this.updateById(medicalRecord);
        // 新增：同步用药明细
        if (medicalRecordDTO.getMedicines() != null) {
            try {
                // 删除旧的用药明细记录
                medicalRecordMedicineMapper.deleteByMedicalRecordId(medicalRecordDTO.getId());
                System.out.println("成功删除旧的用药明细记录");
                
                List<MedicalRecordMedicine> medicines = medicalRecordDTO.getMedicines();
                for (MedicalRecordMedicine m : medicines) {
                    // 新增校验
                    MedicineInventory inventory = medicineInventoryService.getById(m.getMedicineId());
                    if (inventory == null) {
                        throw new BusinessException("药品ID无效，未找到对应药品！");
                    }
                    // 确保设置所有必要的字段
                    m.setMedicalRecordId(medicalRecordDTO.getId());
                    // 如果前端没有传递药品名称和单位，则从库存中获取
                    if (m.getMedicineName() == null || m.getMedicineName().isEmpty()) {
                        m.setMedicineName(inventory.getName());
                    }
                    if (m.getUnit() == null || m.getUnit().isEmpty()) {
                        m.setUnit(inventory.getUnit());
                    }
                    // 确保诊断信息不为空
                    if (m.getDosageUsage() == null) {
                        m.setDosageUsage("");
                    }
                    
                    System.out.println("准备添加用药明细: ID=" + m.getMedicineId() 
                        + ", 名称=" + m.getMedicineName() 
                        + ", 数量=" + m.getQuantity() 
                        + ", 单位=" + m.getUnit() 
                        + ", 用法=" + m.getDosageUsage());
                }
                if (!medicines.isEmpty()) {
                    medicalRecordMedicineMapper.insertBatch(medicines);
                    System.out.println("成功插入" + medicines.size() + "条用药明细记录");
                }
            } catch (Exception e) {
                System.out.println("处理用药明细时出错: " + e.getMessage());
                e.printStackTrace();
                // 不抛出异常，允许病历记录更新继续
            }
        }
        
        // 最后强制查库同步medicineInfo字段
        try {
            List<MedicalRecordMedicine> dbMedicines = medicalRecordMedicineMapper.selectByMedicalRecordId(medicalRecordDTO.getId());
            if (dbMedicines != null && !dbMedicines.isEmpty()) {
                String medicineInfoJson = JSON.toJSONString(dbMedicines);
                medicalRecord.setMedicineInfo(medicineInfoJson);
                this.updateById(medicalRecord);
            }
        } catch (Exception e) {
            System.out.println("同步medicineInfo字段时出错: " + e.getMessage());
            e.printStackTrace();
            // 不抛出异常，允许流程继续
        }
    }

    @Override
    @Transactional
    public void deleteMedicalRecord(Long id) {
        MedicalRecord medicalRecord = this.getById(id);
        if (medicalRecord == null) {
            throw new BusinessException("电子病历记录不存在");
        }
        // 检查关联的预约是否已完成，避免删除已完成预约的病历
        Appointment appointment = appointmentMapper.selectById(medicalRecord.getAppointmentId());
        if (appointment != null && appointment.getStatus() != 2) { // 假设状态 2 表示已完成
            throw new BusinessException("关联的预约未完成，无法删除病历");
        }
        this.removeById(id);
    }

    @Override
    @Transactional
    public void takeMedicine(TakeMedicineDTO dto) {
        MedicalRecord record = this.getById(dto.getRecordId());
        if (record == null) {
            throw new BusinessException("病历不存在");
        }
        Appointment appointment = appointmentMapper.selectById(record.getAppointmentId());
        if (appointment == null || appointment.getStatus() != 2) {
            throw new BusinessException("关联的预约未完成，无法取药");
        }
        if (record.getMedicineInfo() == null) {
            throw new BusinessException("无用药信息");
        }
        // 解析用药明细
        ArrayList<MedicineUsage> medicineList = (ArrayList<MedicineUsage>) JSON.parseArray(record.getMedicineInfo(), MedicineUsage.class);
        for (MedicineUsage usage : medicineList) {
            MedicineInventory inventory = medicineInventoryService.getById(usage.getMedicineId());
            if (inventory == null) throw new BusinessException("药品不存在");
            if (inventory.getStockQuantity() < usage.getQuantity()) {
                throw new BusinessException("药品【" + usage.getName() + "】库存不足");
            }
            int before = inventory.getStockQuantity();
            inventory.setStockQuantity(before - usage.getQuantity());
            medicineInventoryService.updateById(inventory);

            // 写入出库记录
            medicineStockRecordService.createStockOutRecord(
                usage.getMedicineId(),
                usage.getQuantity(),
                10L, // 管理员ID写死
                "用户取药"
            );
        }
        // 更新病历状态
        record.setStatus(2); // 2-已取药
        this.updateById(record);
    }

    @Override
    public List<MedicalRecordDTO> getAllMedicalRecords() {
        List<MedicalRecord> records = this.list();
        return records.stream().map(record -> {
            MedicalRecordDTO dto = convertToDTO(record);
            List<MedicalRecordMedicine> medicines = medicalRecordMedicineMapper.selectByMedicalRecordId(record.getId());
            dto.setMedicines(medicines);
            return dto;
        }).collect(Collectors.toList());
    }

    private MedicalRecordVO convertToVO(MedicalRecord record) {
        MedicalRecordVO vo = new MedicalRecordVO();
        BeanUtils.copyProperties(record, vo);
        vo.setDescription(record.getDescription());

        // 获取医生信息
        Doctor doctor = doctorMapper.selectById(record.getDoctorId());
        if (doctor != null) {
            User doctorUser = userMapper.selectById(doctor.getUserId());
            if (doctorUser != null) {
                vo.setDoctorName(doctorUser.getRealName());
            }
            vo.setDepartmentId(doctor.getDepartmentId());
            Department department = departmentMapper.selectById(doctor.getDepartmentId());
            if (department != null) {
                vo.setDepartmentName(department.getName());
            }
        }

        // 获取患者信息（修正：先查 patient，再查 user）
        Patient patient = patientMapper.selectById(record.getPatientId());
        if (patient != null && patient.getUserId() != null) {
            User patientUser = userMapper.selectById(patient.getUserId());
            if (patientUser != null) {
                vo.setPatientName(patientUser.getRealName());
                vo.setPatientPhone(patientUser.getPhone());
            }
        }

        return vo;
    }

    private MedicalRecordDTO convertToDTO(MedicalRecord medicalRecord) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        BeanUtils.copyProperties(medicalRecord, dto);
        dto.setDescription(medicalRecord.getDescription());

        // 填充非数据库字段（修正：先查 patient，再查 user）
        if (medicalRecord.getPatientId() != null) {
            Patient patient = patientMapper.selectById(medicalRecord.getPatientId());
            if (patient != null && patient.getUserId() != null) {
                User patientUser = userMapper.selectById(patient.getUserId());
                if (patientUser != null) {
                    dto.setPatientName(patientUser.getRealName());
                }
            }
        }

        if (medicalRecord.getDoctorId() != null) {
            Doctor doctor = doctorMapper.selectById(medicalRecord.getDoctorId());
            if (doctor != null && doctor.getUserId() != null) {
                User doctorUser = userMapper.selectById(doctor.getUserId());
                if (doctorUser != null) {
                    dto.setDoctorName(doctorUser.getRealName());
                }
                if (doctor.getDepartmentId() != null) {
                    Department department = departmentMapper.selectById(doctor.getDepartmentId());
                    if (department != null) {
                        dto.setDepartmentName(department.getName());
                    }
                }
            }
        }

        if (medicalRecord.getAppointmentId() != null) {
            Appointment appointment = appointmentMapper.selectById(medicalRecord.getAppointmentId());
            if (appointment != null) {
                dto.setAppointmentNumber(appointment.getAppointmentNumber());
            }
        }
        // visitTime 直接取 medicalRecord 的 visitTime 字段
        if (medicalRecord.getVisitTime() != null) {
            dto.setVisitTime(medicalRecord.getVisitTime().toString());
        }

        return dto;
    }

    @Data
    public static class MedicineUsage {
        private Long id;//明细表主键vice
        private Long medicineId;//药品ID
        private String name;//药品名称
        private Integer quantity;//数量
        private String unit;//单位
        private String dosageUsage;//用法用量
    }

    @Service
    @RequiredArgsConstructor
    public static class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

        private final DoctorMapper doctorMapper;
        private final UserMapper userMapper;
        private final DepartmentMapper departmentMapper;

        @Override
        public Page<DoctorVO> getDoctorList(DoctorQueryDTO queryDTO) {
            Page<Doctor> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

            LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(queryDTO.getRealName())) {
                 LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
                 userWrapper.eq(User::getRealName, queryDTO.getRealName());
                 User user = userMapper.selectOne(userWrapper);
                 if (user != null) {
                     wrapper.eq(Doctor::getUserId, user.getId());
                 } else {
                     return new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
                 }
            }
            wrapper.eq(queryDTO.getDepartmentId() != null, Doctor::getDepartmentId, queryDTO.getDepartmentId());
            wrapper.eq(StringUtils.isNotBlank(queryDTO.getTitle()), Doctor::getTitle, queryDTO.getTitle());
            wrapper.eq(queryDTO.getStatus() != null, Doctor::getStatus, queryDTO.getStatus());
            wrapper.orderByDesc(Doctor::getCreateTime);

            Page<Doctor> doctorPage = doctorMapper.selectPage(page, wrapper);

            Page<DoctorVO> voPage = new Page<>();
            BeanUtils.copyProperties(doctorPage, voPage, "records");

            List<DoctorVO> voList = doctorPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

            voPage.setRecords(voList);
            return voPage;
        }

        @Override
        public DoctorVO getDoctorById(Long id) {
            Doctor doctor = doctorMapper.selectById(id);
            if (doctor == null) {
                throw new BusinessException("医生不存在");
            }
            return convertToVO(doctor);
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public void updateDoctor(Long id, DoctorUpdateDTO updateDTO) {
            Doctor doctor = doctorMapper.selectById(id);
            if (doctor == null) {
                throw new BusinessException("医生不存在");
            }

            User user = userMapper.selectById(doctor.getUserId());
            if (user != null) {
                BeanUtils.copyProperties(updateDTO, user, "id");
                userMapper.updateById(user);
            }

            BeanUtils.copyProperties(updateDTO, doctor, "id", "userId");
            doctorMapper.updateById(doctor);
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public void deleteDoctor(Long id) {
            Doctor doctor = doctorMapper.selectById(id);
            if (doctor == null) {
                throw new BusinessException("医生不存在");
            }

            userMapper.deleteById(doctor.getUserId());
            doctorMapper.deleteById(id);
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public void updateStatus(Long id, Integer status) {
            Doctor doctor = doctorMapper.selectById(id);
            if (doctor == null) {
                throw new BusinessException("医生不存在");
            }

            User user = userMapper.selectById(doctor.getUserId());
            if (user != null) {
                user.setStatus(status);
                userMapper.updateById(user);
            }

            doctor.setStatus(status);
            doctorMapper.updateById(doctor);
        }

        @Override
        public List<DoctorDTO> listAll() {
            List<Doctor> doctors = this.list();
            return doctors.stream().map(this::convertToDTO).collect(Collectors.toList());
        }

        @Override
        public DoctorDTO getById(Long id) {
            Doctor doctor = super.getById(id);
            if (doctor == null) {
                return null;
            }
            return convertToDTO(doctor);
        }

        @Override
        public List<DoctorDTO> listByDepartment(Long departmentId) {
            LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Doctor::getDepartmentId, departmentId);
            List<Doctor> doctors = this.list(wrapper);
            return doctors.stream().map(this::convertToDTO).collect(Collectors.toList());
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public void createDoctor(DoctorUpdateDTO updateDTO) {
            System.out.println("创建医生，医生姓名: " + updateDTO.getDoctorName() + ", 科室ID: " + updateDTO.getDepartmentId());

            // 创建或关联用户账号
            User user = new User();
            BeanUtils.copyProperties(updateDTO, user);
            user.setRole("DOCTOR");
            user.setStatus(1);
            // 如果没有设置真实姓名，则使用医生姓名
            if (StringUtils.isBlank(user.getRealName()) && StringUtils.isNotBlank(updateDTO.getDoctorName())) {
                user.setRealName(updateDTO.getDoctorName());
            }
            userMapper.insert(user);
            System.out.println("创建用户成功，用户ID: " + user.getId());

            // 创建医生记录
            Doctor doctor = new Doctor();
            BeanUtils.copyProperties(updateDTO, doctor);

            // 确保设置医生姓名
            doctor.setDoctorName(updateDTO.getDoctorName());

            // 关联用户ID
            doctor.setUserId(user.getId());

            // 设置科室ID
            if (updateDTO.getDepartmentId() != null) {
                doctor.setDepartmentId(updateDTO.getDepartmentId());
            } else if (StringUtils.isNotBlank(updateDTO.getDepartment())) {
                // 兼容旧代码：根据科室名称查找科室ID
                Department department = departmentMapper.selectOne(
                    new LambdaQueryWrapper<Department>()
                        .eq(Department::getName, updateDTO.getDepartment())
                        .last("LIMIT 1")
                );
                if (department != null) {
                    doctor.setDepartmentId(department.getId());
                } else {
                    throw new BusinessException("科室不存在: " + updateDTO.getDepartment());
                }
            }

            doctorMapper.insert(doctor);
            System.out.println("创建医生成功，医生ID: " + doctor.getId());
        }

        @Override
        public DoctorVO getDoctorByUserId(Long userId) {
            Doctor doctor = doctorMapper.selectOne(
                    new LambdaQueryWrapper<Doctor>()
                            .eq(Doctor::getUserId, userId)
            );
            if (doctor == null) {
                throw new BusinessException("医生不存在");
            }
            return convertToVO(doctor);
        }

        @Override
        public Long getActiveDoctorsCount() {
            LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Doctor::getStatus, 1)
                   .eq(Doctor::getDeleted, 0);
            return doctorMapper.selectCount(wrapper);
        }

        private DoctorVO convertToVO(Doctor doctor) {
            DoctorVO vo = new DoctorVO();
            BeanUtils.copyProperties(doctor, vo);

            System.out.println("医生数据转换，医生ID: " + doctor.getId() + ", 医生姓名: " + doctor.getDoctorName());

            // 优先使用医生表中的doctorName字段
            if (doctor.getDoctorName() != null && !doctor.getDoctorName().isEmpty()) {
                vo.setRealName(doctor.getDoctorName());
            } else {
                // 兼容旧数据，从user表获取
            User user = userMapper.selectById(doctor.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setRealName(user.getRealName());
                vo.setGender(user.getGender());
                vo.setPhone(user.getPhone());
                vo.setEmail(user.getEmail());
                vo.setAvatar(user.getAvatar());
                if (user.getBirthDate() != null) {
                    vo.setAge(Period.between(user.getBirthDate(), LocalDate.now()).getYears());
                    }
                }
            }

            Department department = departmentMapper.selectById(doctor.getDepartmentId());
            if (department != null) {
                vo.setDepartmentName(department.getName());
                System.out.println("获取到科室信息，科室ID: " + department.getId() + ", 科室名称: " + department.getName());
            } else {
                System.out.println("未找到科室信息，科室ID: " + doctor.getDepartmentId());
            }

            return vo;
        }

        private DoctorDTO convertToDTO(Doctor doctor) {
             DoctorDTO dto = new DoctorDTO();
             dto.setId(doctor.getId());
             dto.setUserId(doctor.getUserId());
             dto.setDepartmentId(doctor.getDepartmentId());
             dto.setTitle(doctor.getTitle());
             dto.setSpecialty(doctor.getSpecialty());
             dto.setIntroduction(doctor.getIntroduction());
             dto.setStatus(doctor.getStatus());

            System.out.println("转换医生数据为DTO，医生ID: " + doctor.getId() + ", 医生姓名: " + doctor.getDoctorName());

            // 优先使用医生表中的doctorName字段
            if (doctor.getDoctorName() != null && !doctor.getDoctorName().isEmpty()) {
                dto.setUserName(doctor.getDoctorName());
                System.out.println("使用医生表中的doctorName: " + doctor.getDoctorName());
            } else if (doctor.getUserId() != null) {
                // 兼容旧数据，从user表获取
                  User user = userMapper.selectById(doctor.getUserId());
                  if (user != null) {
                      dto.setUserName(user.getRealName());
                    System.out.println("从user表获取医生姓名: " + user.getRealName());
                  }
             }

             if (doctor.getDepartmentId() != null) {
                 Department department = departmentMapper.selectById(doctor.getDepartmentId());
                 if (department != null) {
                     dto.setDepartmentName(department.getName());
                    System.out.println("获取到科室名称: " + department.getName());
                } else {
                    System.out.println("未找到科室信息，科室ID: " + doctor.getDepartmentId());
                 }
             }

             return dto;
        }
    }
}