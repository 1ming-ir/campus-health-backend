package com.campus.health.mapper;

import com.campus.health.entity.HealthArticle;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HealthArticleMapper {
    List<HealthArticle> findPublished();
    List<HealthArticle> findAll();
    HealthArticle findById(Long id);
    int insert(HealthArticle article);
    int update(HealthArticle article);
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
