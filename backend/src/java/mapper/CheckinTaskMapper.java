package com.dormitory.mapper;

import com.dormitory.entity.CheckinTask;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CheckinTaskMapper {
    
    @Select("SELECT * FROM checkin_task ORDER BY created_at DESC")
    List<CheckinTask> findAll();
    
    @Select("SELECT * FROM checkin_task WHERE id = #{id}")
    CheckinTask findById(Long id);
    
    @Select("SELECT * FROM checkin_task WHERE status = '进行中' AND NOW() BETWEEN start_time AND end_time")
    List<CheckinTask> findActiveTasks();
    
    @Insert("INSERT INTO checkin_task(task_name, description, start_time, end_time, status) " +
            "VALUES(#{taskName}, #{description}, #{startTime}, #{endTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CheckinTask task);
    
    @Update("UPDATE checkin_task SET task_name=#{taskName}, description=#{description}, " +
            "start_time=#{startTime}, end_time=#{endTime}, status=#{status} WHERE id=#{id}")
    int update(CheckinTask task);
    
    @Delete("DELETE FROM checkin_task WHERE id = #{id}")
    int delete(Long id);
    
    @Update("UPDATE checkin_task SET status = '已结束' WHERE id = #{id}")
    int endTask(Long id);
}
