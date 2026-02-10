package com.jiangxia.blog.admin.system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "dept")
public class Dept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "createTime", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "updateTime", nullable = false)
    private LocalDateTime updateTime;

    @Column(name = "deptName", nullable = false, length = 50)
    private String deptName;

    @Column(name = "deptCode", nullable = false, length = 50, unique = true)
    private String deptCode;

    @Column(name = "parentId", nullable = false)
    private Long parentId = 0L;

    @Column(name = "leaderId", length = 50)
    private String leaderId;

    @Column(name = "leaderName", length = 50)
    private String leaderName;

    @Column(name = "orderNum", nullable = false)
    private Integer orderNum = 0;

    @Column(name = "status", nullable = false)
    private Integer status = 1; // 1-正常 0-禁用

    @Column(name = "remark", length = 200)
    private String remark;

    // 用于树形结构展示，不映射到数据库
    @Transient
    private List<Dept> children;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (parentId == null) {
            parentId = 0L;
        }
        if (orderNum == null) {
            orderNum = 0;
        }
        if (status == null) {
            status = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
