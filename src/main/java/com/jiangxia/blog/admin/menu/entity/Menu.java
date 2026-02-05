package com.jiangxia.blog.admin.menu.entity;

import com.jiangxia.blog.admin.system.entity.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @Column(name = "id", nullable = false, length = 255)
    private String id;

    @Column(name = "pid", nullable = false, length = 255)
    private String pid;

    @Column(name = "path", nullable = false, length = 255)
    private String path;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "menuCnName", length = 255)
    private String menuCnName;

    @Column(name = "`order`", nullable = false)
    private Integer orderNum;

    @Column(name = "icon", nullable = false, length = 255)
    private String icon;

    @Column(name = "locale", nullable = false, length = 255)
    private String locale;

    @Column(name = "requiresAuth", nullable = false)
    private Boolean requiresAuth;

    @Column(name = "filePath", nullable = false, length = 255)
    private String filePath;

    @Column(name = "isDelete", nullable = false)
    private Boolean isDelete;

    @ManyToMany(mappedBy = "menus")
    private Set<Role> roles;
}
