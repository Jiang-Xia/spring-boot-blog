package com.jiangxia.blog.admin.system.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuPrivilegeTreeNode {

    private String id;
    private String pid;
    private String path;
    private String name;
    private String menuCnName;
    private Integer orderNum;
    private String icon;
    private String locale;
    private Boolean requiresAuth;
    private String filePath;

    private String label;
    private String value;
    private String type; // menu or privilege

    private List<MenuPrivilegeTreeNode> children = new ArrayList<>();
}
