package com.dormitory.service;

import com.dormitory.entity.Accommodation;
import com.dormitory.entity.Room;
import com.dormitory.entity.Student;
import com.dormitory.mapper.AccommodationMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccommodationService {
    
    @Autowired
    private AccommodationMapper accommodationMapper;
    
    @Autowired
    private RoomMapper roomMapper;
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Transactional
    public void checkIn(String studentId, String buildingNo, String roomNo) {
        Student student = studentMapper.findById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        Accommodation existing = accommodationMapper.findActiveByStudentId(studentId);
        if (existing != null) {
            throw new RuntimeException("学生已入住，请先退宿");
        }
        
        Room room = roomMapper.findById(buildingNo, roomNo);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        if (room.getOccupiedBeds() >= room.getTotalBeds()) {
            throw new RuntimeException("房间已满");
        }
        
        Accommodation accommodation = new Accommodation();
        accommodation.setStudentId(studentId);
        accommodation.setBuildingNo(buildingNo);
        accommodation.setRoomNo(roomNo);
        accommodationMapper.insert(accommodation);
        
        roomMapper.incrementOccupied(buildingNo, roomNo);
        
        student.setStatus("已入住");
        studentMapper.update(student);
    }
    
    @Transactional
    public void transfer(String studentId, String newBuildingNo, String newRoomNo) {
        Accommodation current = accommodationMapper.findActiveByStudentId(studentId);
        if (current == null) {
            throw new RuntimeException("学生未入住");
        }
        
        Room newRoom = roomMapper.findById(newBuildingNo, newRoomNo);
        if (newRoom == null) {
            throw new RuntimeException("新房间不存在");
        }
        if (newRoom.getOccupiedBeds() >= newRoom.getTotalBeds()) {
            throw new RuntimeException("新房间已满");
        }
        
        roomMapper.decrementOccupied(current.getBuildingNo(), current.getRoomNo());
        accommodationMapper.checkOut(studentId);
        
        Accommodation newAccommodation = new Accommodation();
        newAccommodation.setStudentId(studentId);
        newAccommodation.setBuildingNo(newBuildingNo);
        newAccommodation.setRoomNo(newRoomNo);
        accommodationMapper.insert(newAccommodation);
        
        roomMapper.incrementOccupied(newBuildingNo, newRoomNo);
    }
    
    @Transactional
    public void checkOut(String studentId) {
        Accommodation accommodation = accommodationMapper.findActiveByStudentId(studentId);
        if (accommodation == null) {
            throw new RuntimeException("学生未入住");
        }
        
        accommodationMapper.checkOut(studentId);
        roomMapper.decrementOccupied(accommodation.getBuildingNo(), accommodation.getRoomNo());
        
        Student student = studentMapper.findById(studentId);
        student.setStatus("未入住");
        studentMapper.update(student);
    }
}
