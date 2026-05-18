package com.dormitory.mapper;

import com.dormitory.entity.Accommodation;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AccommodationMapper {
    
    @Select("SELECT * FROM accommodation WHERE student_id = #{studentId} AND status = '在住'")
    Accommodation findActiveByStudentId(String studentId);
    
    @Insert("INSERT INTO accommodation(student_id, building_no, room_no, status) " +
            "VALUES(#{studentId}, #{buildingNo}, #{roomNo}, '在住')")
    int insert(Accommodation accommodation);
    
    @Update("UPDATE accommodation SET status = '已退宿', check_out_date = NOW() " +
            "WHERE student_id = #{studentId} AND status = '在住'")
    int checkOut(String studentId);
    
    @Select("SELECT * FROM accommodation WHERE status = '在住'")
    List<Accommodation> findAllActive();
}
