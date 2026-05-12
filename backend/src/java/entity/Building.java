package com.dormitory.entity;

import lombok.Data;

@Data
public class Building {
    private String buildingNo;
    private String buildingName;
    private Integer floors;
    private String genderLimit;
    private String adminPhone;
}
