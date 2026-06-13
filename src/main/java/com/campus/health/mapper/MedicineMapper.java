package com.campus.health.mapper;

import com.campus.health.entity.Medicine;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MedicineMapper {
    @Select("select id, name, category, usage_info as usageInfo, caution, prescription_required as prescriptionRequired, status, created_at as createdAt from medicines where status='PUBLISHED' order by id desc")
    List<Medicine> findPublished();

    @Select("select id, name, category, usage_info as usageInfo, caution, prescription_required as prescriptionRequired, status, created_at as createdAt from medicines order by id desc")
    List<Medicine> findAll();

    @Insert("insert into medicines(name,category,usage_info,caution,prescription_required,status) values(#{name},#{category},#{usageInfo},#{caution},#{prescriptionRequired},#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Medicine medicine);

    @Update("update medicines set name=#{name}, category=#{category}, usage_info=#{usageInfo}, caution=#{caution}, prescription_required=#{prescriptionRequired}, status=#{status} where id=#{id}")
    int update(Medicine medicine);

    @Update("update medicines set status=#{status} where id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}