package com.dormitory.entity;

import lombok.Data;

@Data
public class Room {
    private String buildingNo;
    private String roomNo;
    private Integer totalBeds;
    private Integer occupiedBeds;
    private String remark;
}
