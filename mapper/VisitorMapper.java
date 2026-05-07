package com.dormitory.mapper;

import com.dormitory.entity.Visitor;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VisitorMapper {
    
    @Select("SELECT * FROM visitor ORDER BY entry_time DESC")
    List<Visitor> findAll();
    
    @Insert("INSERT INTO visitor(visitor_name, id_card, visited_student_id, status) " +
            "VALUES(#{visitorName}, #{idCard}, #{visitedStudentId}, '待审批')")
    int insert(Visitor visitor);
    
    @Update("UPDATE visitor SET status = #{status}, admin_remark = #{adminRemark} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("adminRemark") String adminRemark);
    
    @Update("UPDATE visitor SET exit_time = NOW() WHERE id = #{id}")
    int recordExit(Long id);
}
