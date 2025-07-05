package com.medical.medicine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medical.model.dto.MedicineInventoryDTO;
import com.medical.model.entity.MedicineInventory;

import java.util.List;

public interface MedicineInventoryService extends IService<MedicineInventory> {

    /**
     * 创建药品
     */
    void createMedicine(MedicineInventoryDTO medicineInventoryDTO);

    /**
     * 根据ID获取药品详情
     */
    MedicineInventoryDTO getMedicineById(Long id);

    /**
     * 获取所有药品列表
     */
    List<MedicineInventoryDTO> getAllMedicines();

    /**
     * 更新药品信息
     */
    void updateMedicine(MedicineInventoryDTO medicineInventoryDTO);

    /**
     * 删除药品
     */
    void deleteMedicine(Long id);

    /**
     * 更新库存数量
     */
    void updateStockQuantity(Long id, Integer quantityChange);

    /**
     * 检查库存是否低于最低库存
     */
    boolean checkLowStock(Long id);
} 