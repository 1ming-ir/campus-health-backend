package com.campus.health.mapper;

import com.campus.health.entity.Announcement;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AnnouncementMapper {
    @Select("select id, title, type, content, status, created_at as createdAt from announcements where status='PUBLISHED' order by id desc")
    List<Announcement> findPublished();

    @Select("select id, title, type, content, status, created_at as createdAt from announcements order by id desc")
    List<Announcement> findAll();

    @Insert("insert into announcements(title,type,content,status) values(#{title},#{type},#{content},#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);

    @Update("update announcements set title=#{title}, type=#{type}, content=#{content}, status=#{status} where id=#{id}")
    int update(Announcement announcement);

    @Update("update announcements set status=#{status} where id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}