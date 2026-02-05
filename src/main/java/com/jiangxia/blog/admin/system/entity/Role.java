package com.jiangxia.blog.admin.system.entity;

import com.jiangxia.blog.admin.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "createTime", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "updateTime", nullable = false)
    private LocalDateTime updateTime;

    @Column(name = "roleName", nullable = false, length = 255)
    private String roleName;

    @Column(name = "roleDesc", nullable = false, length = 255)
    private String roleDesc;

    @ManyToMany
    @JoinTable(
            name = "role_privileges_privilege",
            joinColumns = @JoinColumn(name = "roleId"),
            inverseJoinColumns = @JoinColumn(name = "privilegeId")
    )
    private Set<Privilege> privileges;

    @ManyToMany
    @JoinTable(
            name = "role_menus_menu",
            joinColumns = @JoinColumn(name = "roleId"),
            inverseJoinColumns = @JoinColumn(name = "menuId")
    )
    private Set<Menu> menus;
}
