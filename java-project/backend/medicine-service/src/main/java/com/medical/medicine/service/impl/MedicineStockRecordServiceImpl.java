package com.medical.medicine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.ApiException;
import com.medical.mapper.MedicineInventoryMapper;
import com.medical.mapper.MedicineStockRecordMapper;
import com.medical.mapper.UserMapper;
import com.medical.medicine.service.MedicineStockRecordService;
import com.medical.model.dto.MedicineStockRecordQueryDTO;
import com.medical.model.entity.MedicineInventory;
import com.medical.model.entity.MedicineStockRecord;
import com.medical.model.entity.User;
import com.medical.model.vo.MedicineStockRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MedicineStockRecordServiceImpl implements MedicineStockRecordService {
    private final MedicineStockRecordMapper stockRecordMapper;
    private final MedicineInventoryMapper medicineMapper;
    private final UserMapper userMapper;

    @Override
    public Page<MedicineStockRecordVO> getStockRecordList(MedicineStockRecordQueryDTO queryDTO) {
        LambdaQueryWrapper<MedicineStockRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getMedicineId() != null, MedicineStockRecord::getMedicineId, queryDTO.getMedicineId())
                .eq(queryDTO.getType() != null, MedicineStockRecord::getType, queryDTO.getType())
                .eq(queryDTO.getOperatorId() != null, MedicineStockRecord::getOperatorId, queryDTO.getOperatorId())
                .ge(queryDTO.getStartTime() != null, MedicineStockRecord::getCreateTime, queryDTO.getStartTime())
                .le(queryDTO.getEndTime() != null, MedicineStockRecord::getCreateTime, queryDTO.getEndTime())
                .orderByDesc(MedicineStockRecord::getCreateTime);

        Page<MedicineStockRecord> page = stockRecordMapper.selectPage(
                new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()),
                wrapper
        );

        Page<MedicineStockRecordVO> voPage = new Page<>();
        BeanUtils.copyProperties(page, voPage, "records");
        voPage.setRecords(page.getRecords().stream().map(this::convertToVO).toList());

        return voPage;
    }

    @Override
    public MedicineStockRecordVO getStockRecordById(Long id) {
        MedicineStockRecord record = stockRecordMapper.selectById(id);
        if (record == null) {
            throw new ApiException("库存记录不存在");
        }
        return convertToVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createStockInRecord(Long medicineId, Integer quantity, Long operatorId, String remark) {
        MedicineInventory medicine = medicineMapper.selectById(medicineId);
        if (medicine == null) {
            throw new ApiException("药品不存在");
        }
        User operator = userMapper.selectById(operatorId);
        if (operator == null) {
            throw new ApiException("操作人不存在");
        }
        MedicineStockRecord record = new MedicineStockRecord();
        record.setMedicineId(medicineId);
        record.setMedicineName(medicine.getName());
        record.setType(1);
        record.setQuantity(quantity);
        record.setBeforeStock(medicine.getStockQuantity());
        record.setAfterStock(medicine.getStockQuantity() + quantity);
        record.setOperatorId(operatorId);
        record.setOperatorName(operator.getRealName());
        record.setRemark(remark);
        record.setOperateTime(LocalDateTime.now());
        stockRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createStockOutRecord(Long medicineId, Integer quantity, Long operatorId, String remark) {
        MedicineInventory medicine = medicineMapper.selectById(medicineId);
        if (medicine == null) {
            throw new ApiException("药品不存在");
        }
        if (medicine.getStockQuantity() < quantity) {
            throw new ApiException("库存不足");
        }
        User operator = userMapper.selectById(operatorId);
        if (operator == null) {
            throw new ApiException("操作人不存在");
        }
        MedicineStockRecord record = new MedicineStockRecord();
        record.setMedicineId(medicineId);
        record.setMedicineName(medicine.getName());
        record.setType(2);
        record.setQuantity(quantity);
        record.setBeforeStock(medicine.getStockQuantity());
        record.setAfterStock(medicine.getStockQuantity() - quantity);
        record.setOperatorId(operatorId);
        record.setOperatorName(operator.getRealName());
        record.setRemark(remark);
        record.setOperateTime(LocalDateTime.now());
        stockRecordMapper.insert(record);
    }

    private MedicineStockRecordVO convertToVO(MedicineStockRecord record) {
        MedicineStockRecordVO vo = new MedicineStockRecordVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
} 