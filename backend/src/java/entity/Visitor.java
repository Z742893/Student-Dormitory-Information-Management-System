package com.dormitory.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Visitor {
    private Long id;
    private String visitorName;
    private String idCard;
    private String visitedStudentId;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private String status;
    private String adminRemark;
}
