package com.dormitory.service;

import com.dormitory.config.JwtUtil;
import com.dormitory.dto.StudentRegisterRequest;
import com.dormitory.entity.Student;
import com.dormitory.entity.User;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public String login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        System.out.println("用户登录: " + username + ", 角色: " + user.getRole() + ", userId: " + user.getId());
        
        // 如果是学生角色，查询学号并添加到token中
        if ("STUDENT".equals(user.getRole())) {
            List<Student> students = studentMapper.findAll();
            System.out.println("查询到 " + students.size() + " 个学生记录");
            
            for (Student student : students) {
                System.out.println("学生: " + student.getStudentId() + ", userId: " + student.getUserId());
                if (student.getUserId() != null && student.getUserId().equals(user.getId())) {
                    System.out.println("找到匹配的学生，学号: " + student.getStudentId());
                    return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole(), student.getStudentId());
                }
            }
            System.out.println("警告: 未找到userId=" + user.getId() + "对应的学生记录");
        }
        
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }
    
    public void register(User user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        userMapper.insert(user);
    }
    
    @Transactional
    public void registerStudent(StudentRegisterRequest request) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建用户账号
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole("STUDENT");
        userMapper.insert(user);
        
        // 生成学号
        String studentId = generateStudentId();
        
        // 创建学生信息
        Student student = new Student();
        student.setStudentId(studentId);
        student.setName(request.getName());
        student.setGender(request.getGender());
        student.setPhone(request.getPhone());
        student.setCollege(request.getCollege());
        student.setClassName(request.getClassName());
        student.setStatus("未入住");
        student.setUserId(user.getId());
        studentMapper.insert(student);
    }
    
    private String generateStudentId() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<Student> allStudents = studentMapper.findAll();
        
        // 找出当前年份的最大序号
        int maxSeq = 0;
        String yearPrefix = String.valueOf(year);
        
        for (Student s : allStudents) {
            if (s.getStudentId() != null && s.getStudentId().startsWith(yearPrefix)) {
                try {
                    int seq = Integer.parseInt(s.getStudentId().substring(4));
                    if (seq > maxSeq) {
                        maxSeq = seq;
                    }
                } catch (NumberFormatException e) {
                    // 忽略格式不正确的学号
                }
            }
        }
        
        // 新序号 = 最大序号 + 1
        int newSeq = maxSeq + 1;
        
        // 格式化为3位数字，不足补0
        return String.format("%d%03d", year, newSeq);
    }
}
