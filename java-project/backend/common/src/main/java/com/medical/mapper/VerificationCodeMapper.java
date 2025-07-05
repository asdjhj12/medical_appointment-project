package com.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.model.entity.VerificationCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {
    
    @Select("SELECT * FROM verification_code WHERE email = #{email} AND type = #{type} AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    VerificationCode selectLatestCode(String email, Integer type);
} 