package com.jiangxia.blog.article.entity;

import com.jiangxia.blog.category.entity.Category;
import com.jiangxia.blog.tag.entity.Tag;
import com.jiangxia.blog.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "createTime", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "updateTime", nullable = false)
    private LocalDateTime updateTime;

    @Column(name = "uTime")
    private LocalDateTime uTime;

    @Column(name = "uid", nullable = false)
    private Long uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useArticles")
    private User user;

    @Column(name = "isDelete", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "topping", nullable = false)
    private Boolean topping = false;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "title", columnDefinition = "TEXT", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "articles")
    private Category category;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "article_tags_tag",
            joinColumns = @JoinColumn(name = "articleId"),
            inverseJoinColumns = @JoinColumn(name = "tagId")
    )
    private Set<Tag> tags;

    @Column(name = "cover", columnDefinition = "LONGTEXT")
    private String cover;

    @Column(name = "likes", nullable = false)
    private Integer likes = 0;

    @Column(name = "views", nullable = false)
    private Integer views = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ArticleStatus status = ArticleStatus.PUBLISH;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "contentHtml", columnDefinition = "LONGTEXT")
    private String contentHtml;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        uTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
        uTime = LocalDateTime.now();
    }
}
