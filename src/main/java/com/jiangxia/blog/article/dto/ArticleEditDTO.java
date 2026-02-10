package com.jiangxia.blog.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleEditDTO {

    @NotNull(message = "文章ID不能为空")
    private Long id;

    private String title;

    private String description;

    private Long categoryId;

    private List<Long> tagIds;

    private String content;

    private String contentHtml;

    private String cover;

    private Boolean isDelete;

    private String status;
}
