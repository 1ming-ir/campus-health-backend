package com.campus.health.mapper;

import com.campus.health.entity.Doctor;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DoctorMapper {
    String SELECT_COLUMNS = "select d.id, d.user_id as userId, u.real_name as realName, d.department, d.title, d.specialty, d.schedule_text as scheduleText, d.phone, d.introduction, d.status from doctors d left join users u on d.user_id=u.id";

    @Select(SELECT_COLUMNS + " order by d.id")
    List<Doctor> findAll();

    @Select(SELECT_COLUMNS + " where d.status='ENABLED' and u.status='ENABLED' order by d.id")
    List<Doctor> findEnabled();

    @Select(SELECT_COLUMNS + " where d.user_id=#{userId}")
    Doctor findByUserId(Long userId);

    @Select(SELECT_COLUMNS + " where d.id=#{id}")
    Doctor findById(Long id);

    @Insert("insert into doctors(user_id,department,title,specialty,schedule_text,phone,introduction,status) values(#{userId},#{department},#{title},#{specialty},#{scheduleText},#{phone},#{introduction},#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Doctor doctor);

    @Update("update doctors set department=#{department}, title=#{title}, specialty=#{specialty}, schedule_text=#{scheduleText}, phone=#{phone}, introduction=#{introduction}, status=#{status} where id=#{id}")
    int update(Doctor doctor);

    @Update("update doctors set status=#{status} where id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Select("select count(*) as total, sum(case when status='ENABLED' then 1 else 0 end) as enabled from doctors")
    Map<String, Object> stats();
}