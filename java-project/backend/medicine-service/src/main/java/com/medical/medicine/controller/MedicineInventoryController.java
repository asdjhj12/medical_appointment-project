package com.medical.medicine.controller;

import com.medical.common.api.Result;
import com.medical.medicine.service.MedicineInventoryService;
import com.medical.model.dto.MedicineInventoryDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "药品库存管理")
@RestController
@RequestMapping("/api/medicine-inventory")
@RequiredArgsConstructor
public class MedicineInventoryController {

    private final MedicineInventoryService medicineInventoryService;

    @ApiOperation("创建药品")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> createMedicine(@Validated @RequestBody MedicineInventoryDTO medicineInventoryDTO) {
        medicineInventoryService.createMedicine(medicineInventoryDTO);
        return Result.success();
    }

    @ApiOperation("根据ID获取药品详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public Result<MedicineInventoryDTO> getMedicineById(@PathVariable Long id) {
        return Result.success(medicineInventoryService.getMedicineById(id));
    }

    @ApiOperation("获取所有药品库存信息")
    @GetMapping
    public Result<List<MedicineInventoryDTO>> getAllMedicines() {
        return Result.success(medicineInventoryService.getAllMedicines());
    }

    @ApiOperation("更新药品信息")
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public Result<Void> updateMedicine(@Validated @RequestBody MedicineInventoryDTO medicineInventoryDTO) {
        medicineInventoryService.updateMedicine(medicineInventoryDTO);
        return Result.success();
    }

    @ApiOperation("删除药品")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> deleteMedicine(@PathVariable Long id) {
        medicineInventoryService.deleteMedicine(id);
        return Result.success();
    }

    @ApiOperation("更新库存数量")
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public Result<Void> updateStockQuantity(@PathVariable Long id, @RequestParam Integer quantityChange) {
        medicineInventoryService.updateStockQuantity(id, quantityChange);
        return Result.success();
    }

    @ApiOperation("检查库存是否低于最低库存")
    @GetMapping("/{id}/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public Result<Boolean> checkLowStock(@PathVariable Long id) {
        return Result.success(medicineInventoryService.checkLowStock(id));
    }

    // 可以根据需要添加分页查询等其他接口
} 