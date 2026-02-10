package com.jiangxia.blog.admin.system.vo;

import lombok.Data;

import java.util.List;

@Data
public class MenuPrivilegeTreeNode {
    
    private String id;
    private String label;
    private String value;
    private String type; // "menu" 或 "privilege"
    private List<MenuPrivilegeTreeNode> children;
    
    // 菜单相关字段
    private String pid;
    private String path;
    private String name;
    private String menuCnName;
    private Integer orderNum;
    
    // 权限相关字段
    private Long privilegeId;
    private String privilegeName;
    private String privilegeCode;
    private String privilegePage;
    private Boolean isVisible;
}
