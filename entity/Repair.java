package com.dormitory.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Repair {
    private Long id;
    private String studentId;
    private String buildingNo;
    private String roomNo;
    private String repairType;
    private String description;
    private String status;
    private LocalDateTime submitTime;
    private LocalDateTime handleTime;
}
