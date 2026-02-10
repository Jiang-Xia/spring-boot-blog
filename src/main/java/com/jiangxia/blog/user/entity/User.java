package com.jiangxia.blog.user.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.jiangxia.blog.admin.system.entity.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "createTime")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Column(name = "updateTime")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Column(name = "isDelete")
    private Boolean isDelete;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "status")
    private UserStatus status;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "username")
    private String username;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "email")
    private String email;

    @Column(name = "githubId")
    private String githubId;

    @Column(name = "password")
    private String password;

    @Column(name = "salt")
    private String salt;

    @Column(name = "intro")
    private String intro;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "homepage")
    @ColumnDefault("''")
    private String homepage;

    @Column(name = "deptId")
    private Integer deptId;

    @ManyToMany
    @JoinTable(
            name = "role_users_user",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private Set<Role> roles;
    
    // 添加必要的辅助方法
    public boolean getIsDelete() {
        return isDelete != null ? isDelete : false;
    }
}