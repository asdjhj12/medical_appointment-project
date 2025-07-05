package com.medical.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class DepartmentDTO {
    private Long id;
    
    @NotBlank(message = "科室名称不能为空")
    @Size(max = 50, message = "科室名称长度不能超过50个字符")
    private String name;
    
    @Size(max = 500, message = "科室描述长度不能超过500个字符")
    private String description;
    
    private Integer status;
} 