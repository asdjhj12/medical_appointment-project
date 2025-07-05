package com.medical.model.entity;

import lombok.Data;

@Data
public class MedicalRecordMedicine {
    private Long id;
    private Long medicalRecordId;
    private Long medicineId;
    private String medicineName;
    private Integer quantity;
    private String unit;
    private String dosageUsage;
} 