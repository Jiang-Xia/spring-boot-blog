package com.jiangxia.blog.article.controller;

import com.jiangxia.blog.article.dto.ArticleCreateDTO;
import com.jiangxia.blog.article.dto.ArticleEditDTO;
import com.jiangxia.blog.article.dto.ArticleListDTO;
import com.jiangxia.blog.article.entity.Article;
import com.jiangxia.blog.article.service.ArticleService;
import com.jiangxia.blog.article.vo.ArticleDetailVO;
import com.jiangxia.blog.article.vo.ArticleListVO;
import com.jiangxia.blog.common.api.ApiResponse;
import com.jiangxia.blog.security.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "文章模块", description = "文章管理相关接口")
@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;
    private final JwtUtil jwtUtil;

    public ArticleController(ArticleService articleService, JwtUtil jwtUtil) {
        this.articleService = articleService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 获取文章列表
     */
    @Operation(summary = "获取文章列表", description = "支持分页、筛选、排序，公开接口无需登录")
    @PostMapping("/list")
    public ArticleListVO getArticleList(
            @RequestBody ArticleListDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long currentUserId = null;
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            currentUserId = jwtUtil.getUserId(jwt);
        }
        
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return articleService.getArticleList(dto, currentUserId);
    }

    /**
     * 获取文章详情（支持ID或标题查询）
     */
    @Operation(summary = "获取文章详情", description = "支持通过ID或文章标题查询，公开接口无需登录")
    @GetMapping("/info")
    public ArticleDetailVO getArticleInfo(@RequestParam("id") String idOrTitle) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return articleService.getArticleByIdOrTitle(idOrTitle);
    }

    /**
     * 创建文章（需要登录）
     */
    @Operation(summary = "创建文章", description = "需要JWT认证，只有登录用户才能创建文章")
    @SecurityRequirement(name = "bearer-jwt")
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public Article createArticle(
            @Valid @RequestBody ArticleCreateDTO dto,
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.substring(7);
        Long userId = jwtUtil.getUserId(jwt);
        
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return articleService.createArticle(dto, userId);
    }

    /**
     * 编辑文章（需要登录）
     */
    @Operation(summary = "编辑文章", description = "需要JWT认证，只能编辑自己的文章")
    @SecurityRequirement(name = "bearer-jwt")
    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public Article updateArticle(
            @Valid @RequestBody ArticleEditDTO dto,
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.substring(7);
        Long userId = jwtUtil.getUserId(jwt);
        
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return articleService.updateArticle(dto, userId);
    }

    /**
     * 删除文章（需要登录）
     */
    @Operation(summary = "删除文章", description = "需要JWT认证，只能删除自己的文章")
    @SecurityRequirement(name = "bearer-jwt")
    @DeleteMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteArticle(
            @RequestParam("id") Long id,
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.substring(7);
        Long userId = jwtUtil.getUserId(jwt);
        
        articleService.deleteArticle(id, userId);
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return "删除成功";
    }

    /**
     * 文章访问量 +1
     */
    @Operation(summary = "文章访问量+1", description = "公开接口，用于统计文章访问数")
    @PostMapping("/views")
    public Boolean updateViews(@RequestParam("id") Long id) {
        articleService.updateViews(id);
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return true;
    }

    /**
     * 文章点赞 +1/-1
     */
    @Operation(summary = "文章点赞", description = "公开接口，status=1表示点赞，status=0表示取消点赞")
    @PostMapping("/likes")
    public Boolean updateLikes(
            @RequestParam("articleId") Long articleId,
            @RequestParam("status") int status) {
        articleService.updateLikes(articleId, status);
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return true;
    }

    /**
     * 禁用/启用文章
     */
    @Operation(summary = "禁用/启用文章", description = "需要JWT认证，用于管理员禁用或启用文章")
    @SecurityRequirement(name = "bearer-jwt")
    @PatchMapping("/disabled")
    @PreAuthorize("isAuthenticated()")
    public Article updateDisabled(
            @RequestParam("id") Long id,
            @RequestParam("isDelete") Boolean isDelete) {
        Article article = articleService.updateArticleField(id, isDelete, null);
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return article;
    }

    /**
     * 置顶/取消置顶文章
     */
    @Operation(summary = "置顶/取消置顶", description = "需要JWT认证，用于管理员设置文章置顶")
    @SecurityRequirement(name = "bearer-jwt")
    @PatchMapping("/topping")
    @PreAuthorize("isAuthenticated()")
    public Article updateTopping(
            @RequestParam("id") Long id,
            @RequestParam("topping") Boolean topping) {
        Article article = articleService.updateArticleField(id, null, topping);
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return article;
    }
}
