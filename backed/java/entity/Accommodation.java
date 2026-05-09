package com.dormitory.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Accommodation {
    private Long id;
    private String studentId;
    private String buildingNo;
    private String roomNo;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private String status;
}
