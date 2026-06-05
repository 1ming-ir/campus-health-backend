package com.campus.health.mapper;

import com.campus.health.entity.Doctor;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DoctorMapper {
    @Select("select d.id, d.user_id, u.real_name, d.department, d.title, d.specialty, d.schedule_text from doctors d left join users u on d.user_id=u.id order by d.id")
    List<Doctor> findAll();

    @Select("select d.id, d.user_id, u.real_name, d.department, d.title, d.specialty, d.schedule_text from doctors d left join users u on d.user_id=u.id where d.id=#{id}")
    Doctor findById(Long id);

    @Insert("insert into doctors(user_id,department,title,specialty,schedule_text) values(#{userId},#{department},#{title},#{specialty},#{scheduleText})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Doctor doctor);

    @Update("update doctors set department=#{department}, title=#{title}, specialty=#{specialty}, schedule_text=#{scheduleText} where id=#{id}")
    int update(Doctor doctor);
}
