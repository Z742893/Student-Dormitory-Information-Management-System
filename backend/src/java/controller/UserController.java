package com.dormitory.controller;

import com.dormitory.dto.LoginRequest;
import com.dormitory.dto.Result;
import com.dormitory.dto.StudentRegisterRequest;
import com.dormitory.entity.User;
import com.dormitory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.login(request.getUsername(), request.getPassword());
            return Result.success(token);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        try {
            userService.register(user);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/register/student")
    public Result<Void> registerStudent(@RequestBody StudentRegisterRequest request) {
        try {
            userService.registerStudent(request);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
