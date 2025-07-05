package com.medical.model.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class MedicineUpdateDTO {
    /**
     * 药品名称
     */
    @NotBlank(message = "药品名称不能为空")
    private String name;

    /**
     * 药品类型
     */
    @NotBlank(message = "药品类型不能为空")
    private String type;

    /**
     * 规格
     */
    @NotBlank(message = "规格不能为空")
    private String specification;

    /**
     * 生产厂家
     */
    @NotBlank(message = "生产厂家不能为空")
    private String manufacturer;

    /**
     * 当前库存
     */
    @NotNull(message = "当前库存不能为空")
    @Min(value = 0, message = "当前库存不能小于0")
    private Integer stock;

    /**
     * 最低库存
     */
    @NotNull(message = "最低库存不能为空")
    @Min(value = 0, message = "最低库存不能小于0")
    private Integer minStock;

    /**
     * 单位
     */
    @NotBlank(message = "单位不能为空")
    private String unit;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @Min(value = 0, message = "单价不能小于0")
    private BigDecimal price;

    /**
     * 状态（0-下架，1-上架）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    private String remark;
} 