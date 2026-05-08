package com.dormitory.dto;

import lombok.Data;

@Data
public class StudentWithAccommodation {
    private String studentId;
    private String name;
    private String gender;
    private String phone;
    private String college;
    private String className;
    private String status;
    private Long userId;
    
    // 住宿信息
    private String buildingNo;
    private String roomNo;
}
