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
    @Select("select id, username, password, real_name, role, phone, status from users where username=#{username}")
    User findByUsername(String username);

    @Select("select id, username, password, real_name, role, phone, status from users where id=#{id}")
    User findById(Long id);

    @Select("select id, username, password, real_name, role, phone, status from users order by id")
    List<User> findAll();

    @Insert("insert into users(username,password,real_name,role,phone,status) values(#{username},#{password},#{realName},#{role},#{phone},'ENABLED')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("update users set status=#{status} where id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
