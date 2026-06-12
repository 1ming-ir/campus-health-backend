package com.campus.health.mapper;

import com.campus.health.entity.User;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    String COLUMNS = "id, username, password, real_name as realName, role, phone, status, college, class_name as className, gender, age, allergy_history as allergyHistory, medical_history as medicalHistory, emergency_contact as emergencyContact";

    @Select("select " + COLUMNS + " from users where username=#{username}")
    User findByUsername(String username);

    @Select("select " + COLUMNS + " from users where id=#{id}")
    User findById(Long id);

    @Select("select " + COLUMNS + " from users order by id")
    List<User> findAll();

    @Insert("insert into users(username,password,real_name,role,phone,status,college,class_name,gender,age,allergy_history,medical_history,emergency_contact) values(#{username},#{password},#{realName},#{role},#{phone},#{status},#{college},#{className},#{gender},#{age},#{allergyHistory},#{medicalHistory},#{emergencyContact})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("update users set status=#{status} where id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Update("update users set real_name=#{realName}, phone=#{phone}, college=#{college}, class_name=#{className}, gender=#{gender}, age=#{age}, allergy_history=#{allergyHistory}, medical_history=#{medicalHistory}, emergency_contact=#{emergencyContact} where id=#{id}")
    int updateProfile(User user);
}