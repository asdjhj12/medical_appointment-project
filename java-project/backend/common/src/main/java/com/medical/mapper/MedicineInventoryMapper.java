package com.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.model.entity.MedicineInventory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MedicineInventoryMapper extends BaseMapper<MedicineInventory> {
    // 可以根据需要添加自定义查询方法
} 