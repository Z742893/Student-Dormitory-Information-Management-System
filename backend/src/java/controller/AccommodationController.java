package com.dormitory.controller;

import com.dormitory.dto.CheckInRequest;
import com.dormitory.dto.Result;
import com.dormitory.dto.TransferRequest;
import com.dormitory.service.AccommodationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accommodation")
public class AccommodationController {
    
    @Autowired
    private AccommodationService accommodationService;
    
    @PostMapping("/checkin")
    public Result<Void> checkIn(@RequestBody CheckInRequest request) {
        try {
            accommodationService.checkIn(request.getStudentId(), 
                request.getBuildingNo(), request.getRoomNo());
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/transfer")
    public Result<Void> transfer(@RequestBody TransferRequest request) {
        try {
            accommodationService.transfer(request.getStudentId(), 
                request.getNewBuildingNo(), request.getNewRoomNo());
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/checkout/{studentId}")
    public Result<Void> checkOut(@PathVariable String studentId) {
        try {
            accommodationService.checkOut(studentId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
