package com.medical.mapper;

import com.medical.model.entity.MedicalRecordMedicine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MedicalRecordMedicineMapper {
    int insertBatch(@Param("list") List<MedicalRecordMedicine> list);

    @Select("SELECT * FROM medical_record_medicine WHERE medical_record_id = #{medicalRecordId}")
    List<MedicalRecordMedicine> selectByMedicalRecordId(@Param("medicalRecordId") Long medicalRecordId);

    int deleteByMedicalRecordId(@Param("medicalRecordId") Long medicalRecordId);
} 