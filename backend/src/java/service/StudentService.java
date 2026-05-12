package com.dormitory.service;

import com.dormitory.dto.StudentWithAccommodation;
import com.dormitory.entity.Student;
import com.dormitory.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {
    
    @Autowired
    private StudentMapper studentMapper;
    
    public List<Student> findAll() {
        return studentMapper.findAll();
    }
    
    public Student findById(String studentId) {
        return studentMapper.findById(studentId);
    }
    
    public void add(Student student) {
        if (student.getStatus() == null) {
            student.setStatus("未入住");
        }
        studentMapper.insert(student);
    }
    
    public void update(Student student) {
        studentMapper.update(student);
    }
    
    public void delete(String studentId) {
        studentMapper.delete(studentId);
    }
    
    public List<StudentWithAccommodation> search(String keyword) {
        return studentMapper.searchWithAccommodation(keyword);
    }
}
