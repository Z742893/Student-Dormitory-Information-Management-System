package com.dormitory.controller;

import com.dormitory.dto.Result;
import com.dormitory.entity.Visitor;
import com.dormitory.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/visitor")
public class VisitorController {
    
    @Autowired
    private VisitorService visitorService;
    
    @GetMapping("/list")
    public Result<List<Visitor>> list() {
        return Result.success(visitorService.findAll());
    }
    
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Visitor visitor) {
        try {
            visitorService.register(visitor);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/approve/{id}")
    public Result<Void> approve(@PathVariable Long id, @RequestParam(required = false) String remark) {
        try {
            visitorService.approve(id, remark);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/reject/{id}")
    public Result<Void> reject(@PathVariable Long id, @RequestParam(required = false) String remark) {
        try {
            visitorService.reject(id, remark);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/exit/{id}")
    public Result<Void> recordExit(@PathVariable Long id) {
        try {
            visitorService.recordExit(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
