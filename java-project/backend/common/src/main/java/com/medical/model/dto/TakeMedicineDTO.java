package com.medical.model.dto;

import lombok.Data;

@Data
public class TakeMedicineDTO {
    private Long recordId;
    private Long userId;
    private String userName;
} 