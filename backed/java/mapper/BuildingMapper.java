package com.dormitory.mapper;

import com.dormitory.entity.Building;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BuildingMapper {
    
    @Select("SELECT * FROM building")
    List<Building> findAll();
    
    @Select("SELECT * FROM building WHERE building_no = #{buildingNo}")
    Building findById(String buildingNo);
    
    @Insert("INSERT INTO building(building_no, building_name, floors, gender_limit, admin_phone) " +
            "VALUES(#{buildingNo}, #{buildingName}, #{floors}, #{genderLimit}, #{adminPhone})")
    int insert(Building building);
    
    @Update("UPDATE building SET building_name=#{buildingName}, floors=#{floors}, " +
            "gender_limit=#{genderLimit}, admin_phone=#{adminPhone} WHERE building_no=#{buildingNo}")
    int update(Building building);
    
    @Delete("DELETE FROM building WHERE building_no = #{buildingNo}")
    int delete(String buildingNo);
}
