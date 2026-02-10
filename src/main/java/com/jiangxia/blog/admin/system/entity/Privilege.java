package com.jiangxia.blog.admin.system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "privilege")
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "createTime", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "updateTime", nullable = false)
    private LocalDateTime updateTime;

    @Column(name = "privilegeName", nullable = false, length = 255)
    private String privilegeName;

    @Column(name = "privilegeCode", nullable = false, length = 255)
    private String privilegeCode;

    @Column(name = "privilegePage", nullable = false, length = 255)
    private String privilegePage;

    @Column(name = "isVisible", nullable = false)
    private Boolean isVisible;

    @Column(name = "pathPattern", nullable = false, length = 500)
    private String pathPattern;

    @Column(name = "httpMethod", nullable = false, length = 10)
    private String httpMethod;

    @Column(name = "isPublic", nullable = false)
    private Boolean isPublic;

    @Column(name = "requireOwnership", nullable = false)
    private Boolean requireOwnership;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToMany(mappedBy = "privileges")
    private Set<Role> roles;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (isVisible == null) {
            isVisible = true;
        }
        if (isPublic == null) {
            isPublic = false;
        }
        if (requireOwnership == null) {
            requireOwnership = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
