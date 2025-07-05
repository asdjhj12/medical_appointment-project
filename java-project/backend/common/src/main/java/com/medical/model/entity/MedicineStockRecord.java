package com.medical.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("medicine_stock_record")
public class MedicineStockRecord extends BaseEntity {
    /**
     * 药品ID
     */
    private Long medicineId;

    /**
     * 药品名称
     */
    private String medicineName;

    /**
     * 类型（1-入库，2-出库）
     */
    private Integer type;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 操作前库存
     */
    private Integer beforeStock;

    /**
     * 操作后库存
     */
    private Integer afterStock;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
} 