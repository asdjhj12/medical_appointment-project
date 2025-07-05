package com.medical.model.dto;

import lombok.Data;

@Data
public class MedicineQueryDTO {
    /**
     * 药品名称
     */
    private String name;

    /**
     * 药品类型
     */
    private String type;

    /**
     * 生产厂家
     */
    private String manufacturer;

    /**
     * 库存状态（0-库存不足，1-库存充足）
     */
    private Integer stockStatus;

    /**
     * 状态（0-下架，1-上架）
     */
    private Integer status;

    /**
     * 分页参数
     */
    private Integer pageNum;
    private Integer pageSize;
} 