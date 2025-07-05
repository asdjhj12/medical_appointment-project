package com.medical.medicine.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.ApiResponse;
import com.medical.medicine.service.MedicineStockRecordService;
import com.medical.model.dto.MedicineStockRecordQueryDTO;
import com.medical.model.vo.MedicineStockRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medicine-stock-records")
@RequiredArgsConstructor
public class MedicineStockRecordController {
    private final MedicineStockRecordService stockRecordService;

    @GetMapping
    public ApiResponse<Page<MedicineStockRecordVO>> getStockRecordList(MedicineStockRecordQueryDTO queryDTO) {
        return ApiResponse.success(stockRecordService.getStockRecordList(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ApiResponse<MedicineStockRecordVO> getStockRecordById(@PathVariable Long id) {
        return ApiResponse.success(stockRecordService.getStockRecordById(id));
    }
} 