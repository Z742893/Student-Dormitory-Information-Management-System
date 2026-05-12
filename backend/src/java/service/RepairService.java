package com.dormitory.service;

import com.dormitory.entity.Repair;
import com.dormitory.mapper.RepairMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RepairService {
    
    @Autowired
    private RepairMapper repairMapper;
    
    public List<Repair> findAll() {
        return repairMapper.findAll();
    }
    
    public List<Repair> findByStudentId(String studentId) {
        return repairMapper.findByStudentId(studentId);
    }
    
    public void submit(Repair repair) {
        repairMapper.insert(repair);
    }
    
    public void updateStatus(Long id, String status) {
        repairMapper.updateStatus(id, status);
    }
}
