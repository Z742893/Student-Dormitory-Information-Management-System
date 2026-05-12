package com.dormitory.service;

import com.dormitory.dto.CheckinStatistics;
import com.dormitory.dto.StudentCheckinStatus;
import com.dormitory.entity.Accommodation;
import com.dormitory.entity.CheckinRecord;
import com.dormitory.entity.CheckinTask;
import com.dormitory.mapper.AccommodationMapper;
import com.dormitory.mapper.CheckinRecordMapper;
import com.dormitory.mapper.CheckinTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckinService {
    
    @Autowired
    private CheckinTaskMapper taskMapper;
    
    @Autowired
    private CheckinRecordMapper recordMapper;
    
    @Autowired
    private AccommodationMapper accommodationMapper;
    
    // ============ 签到任务管理 ============
    
    public List<CheckinTask> findAllTasks() {
        return taskMapper.findAll();
    }
    
    public CheckinTask findTaskById(Long id) {
        return taskMapper.findById(id);
    }
    
    public List<CheckinTask> findActiveTasks() {
        return taskMapper.findActiveTasks();
    }
    
    @Transactional
    public void createTask(CheckinTask task) {
        if (task.getStatus() == null) {
            task.setStatus("进行中");
        }
        taskMapper.insert(task);
    }
    
    @Transactional
    public void updateTask(CheckinTask task) {
        taskMapper.update(task);
    }
    
    @Transactional
    public void deleteTask(Long id) {
        recordMapper.deleteByTaskId(id);
        taskMapper.delete(id);
    }
    
    @Transactional
    public void endTask(Long id) {
        taskMapper.endTask(id);
    }
    
    // ============ 学生签到 ============
    
    @Transactional
    public void checkin(Long taskId, String studentId) {
        // 检查任务是否存在
        CheckinTask task = taskMapper.findById(taskId);
        if (task == null) {
            throw new RuntimeException("签到任务不存在");
        }
        
        // 检查任务是否在进行中
        if (!"进行中".equals(task.getStatus())) {
            throw new RuntimeException("签到任务已结束");
        }
        
        // 检查是否在签到时间范围内
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(task.getStartTime()) || now.isAfter(task.getEndTime())) {
            throw new RuntimeException("不在签到时间范围内");
        }
        
        // 检查学生是否有宿舍
        Accommodation accommodation = accommodationMapper.findActiveByStudentId(studentId);
        if (accommodation == null) {
            throw new RuntimeException("您还未入住宿舍，无需签到");
        }
        
        // 检查是否已签到
        CheckinRecord existing = recordMapper.findByTaskAndStudent(taskId, studentId);
        if (existing != null) {
            throw new RuntimeException("您已签到，请勿重复签到");
        }
        
        // 创建签到记录
        CheckinRecord record = new CheckinRecord();
        record.setTaskId(taskId);
        record.setStudentId(studentId);
        recordMapper.insert(record);
    }
    
    // 检查学生是否已签到某任务
    public boolean hasCheckedIn(Long taskId, String studentId) {
        return recordMapper.findByTaskAndStudent(taskId, studentId) != null;
    }
    
    // ============ 签到统计 ============
    
    public List<StudentCheckinStatus> getStudentCheckinStatus(Long taskId) {
        return recordMapper.getStudentCheckinStatusByTask(taskId);
    }
    
    public CheckinStatistics getTaskStatistics(Long taskId) {
        CheckinTask task = taskMapper.findById(taskId);
        if (task == null) {
            throw new RuntimeException("签到任务不存在");
        }
        
        int totalStudents = recordMapper.countStudentsWithAccommodation();
        int checkedInCount = recordMapper.countByTaskId(taskId);
        int notCheckedInCount = totalStudents - checkedInCount;
        double checkinRate = totalStudents > 0 ? (double) checkedInCount / totalStudents * 100 : 0;
        
        CheckinStatistics stats = new CheckinStatistics();
        stats.setTaskId(taskId);
        stats.setTaskName(task.getTaskName());
        stats.setTotalStudents(totalStudents);
        stats.setCheckedInCount(checkedInCount);
        stats.setNotCheckedInCount(notCheckedInCount);
        stats.setCheckinRate(Math.round(checkinRate * 100.0) / 100.0);
        
        return stats;
    }
}
