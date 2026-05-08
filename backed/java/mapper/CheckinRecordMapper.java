package com.dormitory.mapper;

import com.dormitory.dto.StudentCheckinStatus;
import com.dormitory.entity.CheckinRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CheckinRecordMapper {
    
    @Select("SELECT * FROM checkin_record WHERE task_id = #{taskId}")
    List<CheckinRecord> findByTaskId(Long taskId);
    
    @Select("SELECT * FROM checkin_record WHERE task_id = #{taskId} AND student_id = #{studentId}")
    CheckinRecord findByTaskAndStudent(@Param("taskId") Long taskId, @Param("studentId") String studentId);
    
    @Insert("INSERT INTO checkin_record(task_id, student_id) VALUES(#{taskId}, #{studentId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CheckinRecord record);
    
    @Delete("DELETE FROM checkin_record WHERE task_id = #{taskId}")
    int deleteByTaskId(Long taskId);
    
    @Select("SELECT COUNT(*) FROM checkin_record WHERE task_id = #{taskId}")
    int countByTaskId(Long taskId);
    
    // 获取某个任务的学生签到状态列表（只包含有宿舍的学生）
    @Select("SELECT s.student_id, s.name AS student_name, a.building_no, a.room_no, " +
            "CASE WHEN cr.id IS NOT NULL THEN TRUE ELSE FALSE END AS has_checked_in, " +
            "cr.checkin_time " +
            "FROM student s " +
            "INNER JOIN accommodation a ON s.student_id = a.student_id AND a.status = '在住' " +
            "LEFT JOIN checkin_record cr ON s.student_id = cr.student_id AND cr.task_id = #{taskId} " +
            "ORDER BY a.building_no, a.room_no, s.student_id")
    List<StudentCheckinStatus> getStudentCheckinStatusByTask(Long taskId);
    
    // 统计有宿舍的学生总数
    @Select("SELECT COUNT(DISTINCT s.student_id) FROM student s " +
            "INNER JOIN accommodation a ON s.student_id = a.student_id AND a.status = '在住'")
    int countStudentsWithAccommodation();
}
