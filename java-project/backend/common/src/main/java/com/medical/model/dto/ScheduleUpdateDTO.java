package com.medical.model.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class ScheduleUpdateDTO {
    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    @NotNull(message = "排班日期不能为空")
    @Future(message = "排班日期必须是未来日期")
    private LocalDate scheduleDate;

    @NotNull(message = "时段不能为空")
    private Integer period;

    @NotNull(message = "最大接诊人数不能为空")
    @Positive(message = "最大接诊人数必须大于0")
    private Integer maxPatients;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private String timeSlot;
} 