package com.jiangxia.blog.article.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleListVO {

    private List<ArticleItemVO> list;

    private Pagination pagination;

    @Getter
    @Setter
    public static class Pagination {
        private Integer page;
        private Integer pageSize;
        private Long total;
        private Integer totalPage;
    }
}
