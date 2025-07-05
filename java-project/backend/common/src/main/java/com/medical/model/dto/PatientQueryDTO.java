package com.medical.model.dto;

import com.medical.common.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientQueryDTO extends PageParam {
    private String realName;
    private String phone;
    private String idCard;
} 