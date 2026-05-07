package com.dormitory.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentCheckinStatus {
    private String studentId;
    private String studentName;
    private String buildingNo;
    private String roomNo;
    private Boolean hasCheckedIn;
    private LocalDateTime checkinTime;
}
