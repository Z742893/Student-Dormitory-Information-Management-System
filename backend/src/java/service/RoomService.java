package com.dormitory.service;

import com.dormitory.entity.Room;
import com.dormitory.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomService {
    
    @Autowired
    private RoomMapper roomMapper;
    
    public List<Room> findByBuilding(String buildingNo) {
        return roomMapper.findByBuilding(buildingNo);
    }
    
    public Room findById(String buildingNo, String roomNo) {
        return roomMapper.findById(buildingNo, roomNo);
    }
    
    public void add(Room room) {
        if (room.getOccupiedBeds() == null) {
            room.setOccupiedBeds(0);
        }
        roomMapper.insert(room);
    }
    
    public void update(Room room) {
        roomMapper.update(room);
    }
    
    public void delete(String buildingNo, String roomNo) {
        roomMapper.delete(buildingNo, roomNo);
    }
    
    public List<Room> findAvailableRooms(String buildingNo) {
        return roomMapper.findAvailableRooms(buildingNo);
    }
}
