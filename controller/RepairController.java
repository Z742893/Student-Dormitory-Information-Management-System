package com.dormitory.controller;

import com.dormitory.dto.Result;
import com.dormitory.entity.Repair;
import com.dormitory.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/repair")
public class RepairController {
    
    @Autowired
    private RepairService repairService;
    
    @GetMapping("/list")
    public Result<List<Repair>> list() {
        return Result.success(repairService.findAll());
    }
    
    @GetMapping("/student/{studentId}")
    public Result<List<Repair>> listByStudent(@PathVariable String studentId) {
        return Result.success(repairService.findByStudentId(studentId));
    }
    
    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody Repair repair) {
        try {
            repairService.submit(repair);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            repairService.updateStatus(id, status);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
