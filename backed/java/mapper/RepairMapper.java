package com.dormitory.mapper;

import com.dormitory.entity.Repair;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RepairMapper {
    
    @Select("SELECT * FROM repair ORDER BY submit_time DESC")
    List<Repair> findAll();
    
    @Select("SELECT * FROM repair WHERE student_id = #{studentId} ORDER BY submit_time DESC")
    List<Repair> findByStudentId(String studentId);
    
    @Insert("INSERT INTO repair(student_id, building_no, room_no, repair_type, description, status) " +
            "VALUES(#{studentId}, #{buildingNo}, #{roomNo}, #{repairType}, #{description}, '待处理')")
    int insert(Repair repair);
    
    @Update("UPDATE repair SET status = #{status}, handle_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
