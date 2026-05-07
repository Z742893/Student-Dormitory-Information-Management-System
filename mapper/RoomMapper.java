package com.dormitory.mapper;

import com.dormitory.entity.Room;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RoomMapper {
    
    @Select("SELECT * FROM room WHERE building_no = #{buildingNo}")
    List<Room> findByBuilding(String buildingNo);
    
    @Select("SELECT * FROM room WHERE building_no = #{buildingNo} AND room_no = #{roomNo}")
    Room findById(@Param("buildingNo") String buildingNo, @Param("roomNo") String roomNo);
    
    @Insert("INSERT INTO room(building_no, room_no, total_beds, occupied_beds, remark) " +
            "VALUES(#{buildingNo}, #{roomNo}, #{totalBeds}, #{occupiedBeds}, #{remark})")
    int insert(Room room);
    
    @Update("UPDATE room SET total_beds=#{totalBeds}, occupied_beds=#{occupiedBeds}, " +
            "remark=#{remark} WHERE building_no=#{buildingNo} AND room_no=#{roomNo}")
    int update(Room room);
    
    @Delete("DELETE FROM room WHERE building_no = #{buildingNo} AND room_no = #{roomNo}")
    int delete(@Param("buildingNo") String buildingNo, @Param("roomNo") String roomNo);
    
    @Select("SELECT * FROM room WHERE building_no = #{buildingNo} AND occupied_beds < total_beds")
    List<Room> findAvailableRooms(String buildingNo);
    
    @Update("UPDATE room SET occupied_beds = occupied_beds + 1 " +
            "WHERE building_no = #{buildingNo} AND room_no = #{roomNo}")
    int incrementOccupied(@Param("buildingNo") String buildingNo, @Param("roomNo") String roomNo);
    
    @Update("UPDATE room SET occupied_beds = occupied_beds - 1 " +
            "WHERE building_no = #{buildingNo} AND room_no = #{roomNo}")
    int decrementOccupied(@Param("buildingNo") String buildingNo, @Param("roomNo") String roomNo);
}
