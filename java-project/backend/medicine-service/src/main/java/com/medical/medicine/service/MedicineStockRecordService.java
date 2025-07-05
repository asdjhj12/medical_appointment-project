package com.medical.medicine.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.model.dto.MedicineStockRecordQueryDTO;
import com.medical.model.vo.MedicineStockRecordVO;

public interface MedicineStockRecordService {
    /**
     * 获取库存记录列表
     */
    Page<MedicineStockRecordVO> getStockRecordList(MedicineStockRecordQueryDTO queryDTO);

    /**
     * 获取库存记录详情
     */
    MedicineStockRecordVO getStockRecordById(Long id);

    /**
     * 创建入库记录
     */
    void createStockInRecord(Long medicineId, Integer quantity, Long operatorId, String remark);

    /**
     * 创建出库记录
     */
    void createStockOutRecord(Long medicineId, Integer quantity, Long operatorId, String remark);
} 