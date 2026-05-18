package com.dormitory.entity;

import lombok.Data;

@Data
public class Student {
    private String studentId;
    private String name;
    private String gender;
    private String phone;
    private String college;
    private String className;
    private String status;
    private Long userId;
}
