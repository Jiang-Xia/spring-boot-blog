package com.jiangxia.blog.article.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ArticleItemVO {

    private Long id;

    private String title;

    private String description;

    private String cover;

    private Integer likes;

    private Integer views;

    private Integer commentCount;

    private Boolean isDelete;

    private Boolean topping;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime uTime;

    private CategoryVO category;

    private List<TagVO> tags;

    private UserInfo userInfo;

    @Getter
    @Setter
    public static class CategoryVO {
        private Long id;
        private String label;
        private String value;
        private String color;
    }

    @Getter
    @Setter
    public static class TagVO {
        private Long id;
        private String label;
        private String value;
        private String color;
    }

    @Getter
    @Setter
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String avatar;
    }
}
