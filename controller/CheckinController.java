package com.dormitory.controller;

import com.dormitory.dto.CheckinStatistics;
import com.dormitory.dto.Result;
import com.dormitory.dto.StudentCheckinStatus;
import com.dormitory.entity.CheckinTask;
import com.dormitory.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkin")
public class CheckinController {
    
    @Autowired
    private CheckinService checkinService;
    
    // ============ 签到任务管理（管理员） ============
    
    @GetMapping("/task/list")
    public Result<List<CheckinTask>> listTasks() {
        return Result.success(checkinService.findAllTasks());
    }
    
    @GetMapping("/task/{id}")
    public Result<CheckinTask> getTask(@PathVariable Long id) {
        return Result.success(checkinService.findTaskById(id));
    }
    
    @PostMapping("/task/add")
    public Result<Void> addTask(@RequestBody CheckinTask task) {
        try {
            checkinService.createTask(task);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/task/update")
    public Result<Void> updateTask(@RequestBody CheckinTask task) {
        try {
            checkinService.updateTask(task);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/task/{id}")
    public Result<Void> deleteTask(@PathVariable Long id) {
        try {
            checkinService.deleteTask(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/task/end/{id}")
    public Result<Void> endTask(@PathVariable Long id) {
        try {
            checkinService.endTask(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // ============ 签到统计（管理员） ============
    
    @GetMapping("/statistics/{taskId}")
    public Result<CheckinStatistics> getStatistics(@PathVariable Long taskId) {
        try {
            return Result.success(checkinService.getTaskStatistics(taskId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/status/{taskId}")
    public Result<List<StudentCheckinStatus>> getStudentStatus(@PathVariable Long taskId) {
        return Result.success(checkinService.getStudentCheckinStatus(taskId));
    }
    
    // ============ 学生签到 ============
    
    @GetMapping("/active")
    public Result<List<CheckinTask>> getActiveTasks() {
        return Result.success(checkinService.findActiveTasks());
    }
    
    @PostMapping("/do/{taskId}/{studentId}")
    public Result<Void> doCheckin(@PathVariable Long taskId, @PathVariable String studentId) {
        try {
            checkinService.checkin(taskId, studentId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/check/{taskId}/{studentId}")
    public Result<Boolean> checkStatus(@PathVariable Long taskId, @PathVariable String studentId) {
        return Result.success(checkinService.hasCheckedIn(taskId, studentId));
    }
}
