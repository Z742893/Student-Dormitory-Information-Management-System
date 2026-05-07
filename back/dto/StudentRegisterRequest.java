package com.dormitory.dto;

import lombok.Data;

@Data
public class StudentRegisterRequest {
    private String username;
    private String password;
    private String name;
    private String gender;
    private String phone;
    private String college;
    private String className;
}
