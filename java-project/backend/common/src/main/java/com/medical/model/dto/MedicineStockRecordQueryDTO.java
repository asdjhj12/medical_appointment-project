package com.medical.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicineStockRecordQueryDTO {
    private Long medicineId;
    private String medicineName;
    private String type;
    private String manufacturer;
    private Long operatorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer pageNum;
    private Integer pageSize;
} 