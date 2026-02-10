package com.jiangxia.blog.category.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.jiangxia.blog.article.entity.Article;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "createTime", nullable = false)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Column(name = "updateTime", nullable = false)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Column(name = "label", nullable = false, length = 255)
    private String label;

    @Column(name = "value", nullable = false, length = 255)
    private String value;

    @Column(name = "color", nullable = false, length = 50)
    private String color;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Article> articles;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
