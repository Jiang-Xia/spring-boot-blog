package com.jiangxia.blog.admin.system.service;

import com.jiangxia.blog.admin.menu.entity.Menu;
import com.jiangxia.blog.admin.menu.repository.MenuRepository;
import com.jiangxia.blog.admin.system.entity.Privilege;
import com.jiangxia.blog.admin.system.repository.PrivilegeRepository;
import com.jiangxia.blog.admin.system.vo.MenuPrivilegeTreeNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleServiceSupport {

    private final MenuRepository menuRepository;
    private final PrivilegeRepository privilegeRepository;

    public RoleServiceSupport(MenuRepository menuRepository,
                              PrivilegeRepository privilegeRepository) {
        this.menuRepository = menuRepository;
        this.privilegeRepository = privilegeRepository;
    }

    /**
     * 获取菜单 + 权限树结构，对标 Nest RoleService.getMenuPrivilegeTree
     */
    @Transactional(readOnly = true)
    public List<MenuPrivilegeTreeNode> getMenuPrivilegeTree() {
        List<Menu> menus = menuRepository.findAll(Sort.by(Sort.Direction.ASC, "orderNum"));
        List<Privilege> privileges = privilegeRepository.findAll();

        Map<String, List<Privilege>> menuIdToPrivileges = privileges.stream()
                .collect(Collectors.groupingBy(Privilege::getPrivilegePage));

        Map<String, MenuPrivilegeTreeNode> menuMap = new LinkedHashMap<>();
        for (Menu menu : menus) {
            MenuPrivilegeTreeNode node = new MenuPrivilegeTreeNode();
            node.setId(menu.getId());
            node.setPid(menu.getPid());
            node.setPath(menu.getPath());
            node.setName(menu.getName());
            node.setMenuCnName(menu.getMenuCnName());
            node.setOrderNum(menu.getOrderNum());
//            node.setIcon(menu.getIcon());
//            node.setLocale(menu.getLocale());
//            node.setRequiresAuth(menu.getRequiresAuth());
//            node.setFilePath(menu.getFilePath());
            node.setLabel(menu.getMenuCnName() != null && !menu.getMenuCnName().isEmpty()
                    ? menu.getMenuCnName() : menu.getName());
            node.setValue(menu.getId());
            node.setType("menu");
            menuMap.put(menu.getId(), node);
        }

        // 组装权限到对应菜单下
        for (Menu menu : menus) {
            MenuPrivilegeTreeNode current = menuMap.get(menu.getId());
            List<Privilege> menuPrivileges = menuIdToPrivileges.getOrDefault(menu.getId(), Collections.emptyList());
            for (Privilege p : menuPrivileges) {
                MenuPrivilegeTreeNode privilegeNode = new MenuPrivilegeTreeNode();
                privilegeNode.setId(String.valueOf(p.getId()));
                privilegeNode.setPid(menu.getId());
                privilegeNode.setLabel(p.getPrivilegeName());
                privilegeNode.setValue(String.valueOf(p.getId()));
                privilegeNode.setType("privilege");
                current.getChildren().add(privilegeNode);
            }
        }

        // 构建菜单树
        List<MenuPrivilegeTreeNode> roots = new ArrayList<>();
        for (Menu menu : menus) {
            MenuPrivilegeTreeNode current = menuMap.get(menu.getId());
            if ("0".equals(menu.getPid()) || menu.getPid() == null || menu.getPid().isEmpty()) {
                roots.add(current);
            } else {
                MenuPrivilegeTreeNode parent = menuMap.get(menu.getPid());
                if (parent != null) {
                    parent.getChildren().add(current);
                } else {
                    roots.add(current);
                }
            }
        }

        return roots;
    }
}
