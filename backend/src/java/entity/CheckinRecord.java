package com.dormitory.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CheckinRecord {
    private Long id;
    private Long taskId;
    private String studentId;
    private LocalDateTime checkinTime;
}
