package com.medical.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class MedicineInventoryDTO {
    private Long id;

    @NotBlank(message = "药品名称不能为空")
    @Size(max = 100, message = "药品名称长度不能超过100个字符")
    private String name;

    @Size(max = 100, message = "规格长度不能超过100个字符")
    private String specification;

    @Size(max = 100, message = "制造商长度不能超过100个字符")
    private String manufacturer;

    @Size(max = 50, message = "类别长度不能超过50个字符")
    private String category;

    @Size(max = 20, message = "单位长度不能超过20个字符")
    private String unit;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @NotNull(message = "库存数量不能为空")
    private Integer stockQuantity;

    @NotNull(message = "最低库存不能为空")
    private Integer minStock;

    private Integer status;

    private String createTime;
    private String updateTime;
} 