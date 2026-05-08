package com.dormitory.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private String studentId;
    private String newBuildingNo;
    private String newRoomNo;
}
