package com.dormitory.service;

import com.dormitory.entity.Building;
import com.dormitory.mapper.BuildingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BuildingService {
    
    @Autowired
    private BuildingMapper buildingMapper;
    
    public List<Building> findAll() {
        return buildingMapper.findAll();
    }
    
    public Building findById(String buildingNo) {
        return buildingMapper.findById(buildingNo);
    }
    
    public void add(Building building) {
        buildingMapper.insert(building);
    }
    
    public void update(Building building) {
        buildingMapper.update(building);
    }
    
    public void delete(String buildingNo) {
        buildingMapper.delete(buildingNo);
    }
}
