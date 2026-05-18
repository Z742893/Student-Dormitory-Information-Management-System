package com.dormitory.controller;

import com.dormitory.dto.Result;
import com.dormitory.entity.Building;
import com.dormitory.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/building")
public class BuildingController {
    
    @Autowired
    private BuildingService buildingService;
    
    @GetMapping("/list")
    public Result<List<Building>> list() {
        return Result.success(buildingService.findAll());
    }
    
    @GetMapping("/{id}")
    public Result<Building> getById(@PathVariable String id) {
        return Result.success(buildingService.findById(id));
    }
    
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Building building) {
        try {
            buildingService.add(building);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Building building) {
        try {
            buildingService.update(building);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        try {
            buildingService.delete(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
