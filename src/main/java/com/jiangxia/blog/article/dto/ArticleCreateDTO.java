package com.jiangxia.blog.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleCreateDTO {

    @NotBlank(message = "请输入文章标题")
    private String title;

    @NotBlank(message = "请输入文章描述")
    private String description;

    @NotNull(message = "请选择文章分类")
    private Long categoryId;

    @NotNull(message = "请选择文章标签")
    private List<Long> tagIds;

    @NotBlank(message = "请输入文章内容")
    private String content;

    @NotBlank(message = "请输入文章内容HTML")
    private String contentHtml;

    private String cover;

    private String status; // draft 或 publish
}
