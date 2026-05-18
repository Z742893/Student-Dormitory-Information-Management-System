package com.dormitory.dto;

import lombok.Data;

@Data
public class CheckinStatistics {
    private Long taskId;
    private String taskName;
    private Integer totalStudents;      // 有宿舍的学生总数
    private Integer checkedInCount;     // 已签到人数
    private Integer notCheckedInCount;  // 未签到人数
    private Double checkinRate;         // 签到率
}
