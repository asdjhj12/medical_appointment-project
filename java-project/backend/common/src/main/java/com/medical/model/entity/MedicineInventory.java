package com.medical.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("medicine_inventory")
public class MedicineInventory {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String specification;

    private String manufacturer;

    private String category;

    private String unit;

    private BigDecimal price;

    private Integer stockQuantity;

    private Integer minStock;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
} 