package com.dormitory.service;

import com.dormitory.entity.Visitor;
import com.dormitory.mapper.VisitorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VisitorService {
    
    @Autowired
    private VisitorMapper visitorMapper;
    
    public List<Visitor> findAll() {
        return visitorMapper.findAll();
    }
    
    public void register(Visitor visitor) {
        visitorMapper.insert(visitor);
    }
    
    public void approve(Long id, String adminRemark) {
        visitorMapper.updateStatus(id, "已授权", adminRemark);
    }
    
    public void reject(Long id, String adminRemark) {
        visitorMapper.updateStatus(id, "已拒绝", adminRemark);
    }
    
    public void recordExit(Long id) {
        visitorMapper.recordExit(id);
    }
}
