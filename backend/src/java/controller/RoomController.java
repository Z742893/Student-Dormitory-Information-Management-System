package com.dormitory.controller;

import com.dormitory.dto.Result;
import com.dormitory.entity.Room;
import com.dormitory.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/room")
public class RoomController {
    
    @Autowired
    private RoomService roomService;
    
    @GetMapping("/list/{buildingNo}")
    public Result<List<Room>> listByBuilding(@PathVariable String buildingNo) {
        return Result.success(roomService.findByBuilding(buildingNo));
    }
    
    @GetMapping("/available/{buildingNo}")
    public Result<List<Room>> availableRooms(@PathVariable String buildingNo) {
        return Result.success(roomService.findAvailableRooms(buildingNo));
    }
    
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Room room) {
        try {
            roomService.add(room);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Room room) {
        try {
            roomService.update(room);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{buildingNo}/{roomNo}")
    public Result<Void> delete(@PathVariable String buildingNo, @PathVariable String roomNo) {
        try {
            roomService.delete(buildingNo, roomNo);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
