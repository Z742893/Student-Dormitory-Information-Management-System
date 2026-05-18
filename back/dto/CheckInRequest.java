package com.dormitory.dto;

import lombok.Data;

@Data
public class CheckInRequest {
    private String studentId;
    private String buildingNo;
    private String roomNo;
}
