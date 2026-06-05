package com.campus.health.controller;

import com.campus.health.common.ApiResponse;
import com.campus.health.entity.HealthArticle;
import com.campus.health.mapper.HealthArticleMapper;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final HealthArticleMapper articleMapper;

    public ArticleController(HealthArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @GetMapping
    public ApiResponse<List<HealthArticle>> list() {
        return ApiResponse.ok(articleMapper.findPublished());
    }

    @GetMapping("/{id}")
    public ApiResponse<HealthArticle> detail(@PathVariable Long id) {
        HealthArticle article = articleMapper.findById(id);
        if (article == null) {
            return ApiResponse.fail("文章不存在");
        }
        return ApiResponse.ok(article);
    }
}
