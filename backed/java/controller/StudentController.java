package com.dormitory.controller;

import com.dormitory.dto.Result;
import com.dormitory.dto.StudentWithAccommodation;
import com.dormitory.entity.Student;
import com.dormitory.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @GetMapping("/list")
    public Result<List<Student>> list() {
        return Result.success(studentService.findAll());
    }
    
    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable String id) {
        return Result.success(studentService.findById(id));
    }
    
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Student student) {
        try {
            studentService.add(student);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Student student) {
        try {
            studentService.update(student);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        try {
            studentService.delete(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public Result<List<StudentWithAccommodation>> search(@RequestParam String keyword) {
        return Result.success(studentService.search(keyword));
    }
}
