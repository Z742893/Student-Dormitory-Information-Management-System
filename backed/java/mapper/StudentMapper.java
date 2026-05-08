package com.dormitory.mapper;

import com.dormitory.dto.StudentWithAccommodation;
import com.dormitory.entity.Student;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface StudentMapper {
    
    @Select("SELECT * FROM student")
    List<Student> findAll();
    
    @Select("SELECT * FROM student WHERE student_id = #{studentId}")
    Student findById(String studentId);
    
    @Insert("INSERT INTO student(student_id, name, gender, phone, college, class_name, status, user_id) " +
            "VALUES(#{studentId}, #{name}, #{gender}, #{phone}, #{college}, #{className}, #{status}, #{userId})")
    int insert(Student student);
    
    @Update("UPDATE student SET name=#{name}, gender=#{gender}, phone=#{phone}, " +
            "college=#{college}, class_name=#{className}, status=#{status} WHERE student_id=#{studentId}")
    int update(Student student);
    
    @Delete("DELETE FROM student WHERE student_id = #{studentId}")
    int delete(String studentId);
    
    @Select("SELECT s.student_id, s.name, s.gender, s.phone, s.college, s.class_name, s.status, s.user_id, " +
            "a.building_no, a.room_no FROM student s " +
            "LEFT JOIN accommodation a ON s.student_id = a.student_id AND a.status = '在住' " +
            "WHERE s.student_id = #{keyword} OR s.name LIKE CONCAT('%', #{keyword}, '%')")
    List<StudentWithAccommodation> searchWithAccommodation(String keyword);
}
