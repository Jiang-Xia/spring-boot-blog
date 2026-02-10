package com.jiangxia.blog.article.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleListDTO {

    private Integer page = 1;

    private Integer pageSize = 10;

    private Long categoryId;

    private String title;

    private String content;

    private String description;

    private String sort; // ASC 或 DESC

    private List<Long> tagIds;

    private Boolean client; // 是否是客户端请求

    private Boolean admin; // 是否是管理端请求

    private Boolean onlyMy; // 是否只查询自己的文章
}
