package com.medical.medicine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.common.exception.BusinessException;
import com.medical.mapper.MedicineInventoryMapper;
import com.medical.medicine.service.MedicineInventoryService;
import com.medical.model.dto.MedicineInventoryDTO;
import com.medical.model.entity.MedicineInventory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineInventoryServiceImpl extends ServiceImpl<MedicineInventoryMapper, MedicineInventory> implements MedicineInventoryService {

    private final MedicineInventoryMapper medicineInventoryMapper;

    @Override
    @Transactional
    public void createMedicine(MedicineInventoryDTO medicineInventoryDTO) {
        // 检查药品名称是否已存在
        LambdaQueryWrapper<MedicineInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineInventory::getName, medicineInventoryDTO.getName())
               .eq(MedicineInventory::getDeleted, 0); // 检查未删除的药品
        if (medicineInventoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("药品名称已存在");
        }

        MedicineInventory medicineInventory = new MedicineInventory();
        BeanUtils.copyProperties(medicineInventoryDTO, medicineInventory);
        // 默认状态为1 (正常)
        if (medicineInventory.getStatus() == null) {
            medicineInventory.setStatus(1);
        }
        this.save(medicineInventory);
    }

    @Override
    public MedicineInventoryDTO getMedicineById(Long id) {
        MedicineInventory medicineInventory = this.getById(id);
        if (medicineInventory == null) {
            throw new BusinessException("药品不存在");
        }
        return convertToDTO(medicineInventory);
    }

    @Override
    public List<MedicineInventoryDTO> getAllMedicines() {
        LambdaQueryWrapper<MedicineInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineInventory::getDeleted, 0);
        List<MedicineInventory> medicineList = this.list(wrapper);
        return medicineList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateMedicine(MedicineInventoryDTO medicineInventoryDTO) {
        MedicineInventory medicineInventory = this.getById(medicineInventoryDTO.getId());
        if (medicineInventory == null) {
            throw new BusinessException("药品不存在");
        }

        // 检查更新后的药品名称是否已存在（排除当前药品）
        LambdaQueryWrapper<MedicineInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineInventory::getName, medicineInventoryDTO.getName())
               .eq(MedicineInventory::getDeleted, 0)
               .ne(MedicineInventory::getId, medicineInventoryDTO.getId());
        if (medicineInventoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("药品名称已存在");
        }

        BeanUtils.copyProperties(medicineInventoryDTO, medicineInventory, "id", "createTime", "deleted"); // 不允许修改ID和创建时间、删除标记
        this.updateById(medicineInventory);
    }

    @Override
    @Transactional
    public void deleteMedicine(Long id) {
        MedicineInventory medicineInventory = this.getById(id);
        if (medicineInventory == null) {
            throw new BusinessException("药品不存在");
        }
        // 使用逻辑删除
        this.removeById(id);
    }

    @Override
    @Transactional
    public void updateStockQuantity(Long id, Integer quantityChange) {
        MedicineInventory medicineInventory = this.getById(id);
        if (medicineInventory == null) {
            throw new BusinessException("药品不存在");
        }

        int newStock = medicineInventory.getStockQuantity() + quantityChange;
        if (newStock < 0) {
            throw new BusinessException("库存数量不足");
        }

        medicineInventory.setStockQuantity(newStock);
        this.updateById(medicineInventory);

        // 检查是否低于最低库存
        if (newStock < medicineInventory.getMinStock()) {
            // TODO: 发送低库存警报通知
            System.out.println("药品 " + medicineInventory.getName() + " 库存低于最低库存！当前库存: " + newStock + ", 最低库存: " + medicineInventory.getMinStock());
        }
    }

    @Override
    public boolean checkLowStock(Long id) {
        MedicineInventory medicineInventory = this.getById(id);
        if (medicineInventory == null) {
            throw new BusinessException("药品不存在");
        }
        return medicineInventory.getStockQuantity() < medicineInventory.getMinStock();
    }

    private MedicineInventoryDTO convertToDTO(MedicineInventory medicineInventory) {
        MedicineInventoryDTO dto = new MedicineInventoryDTO();
        BeanUtils.copyProperties(medicineInventory, dto);
        return dto;
    }
} 