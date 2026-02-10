package com.jiangxia.blog.admin.system.service;

import com.jiangxia.blog.admin.menu.entity.Menu;
import com.jiangxia.blog.admin.menu.repository.MenuRepository;
import com.jiangxia.blog.admin.system.dto.CreateRoleDTO;
import com.jiangxia.blog.admin.system.dto.UpdateRoleDTO;
import com.jiangxia.blog.admin.system.entity.Privilege;
import com.jiangxia.blog.admin.system.entity.Role;
import com.jiangxia.blog.admin.system.repository.PrivilegeRepository;
import com.jiangxia.blog.admin.system.repository.RoleRepository;
import com.jiangxia.blog.admin.system.vo.MenuPrivilegeTreeNode;
import com.jiangxia.blog.admin.system.vo.RoleListVO;
import com.jiangxia.blog.common.exception.BizException;
import com.jiangxia.blog.common.vo.Pagination;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final MenuRepository menuRepository;

    public RoleService(RoleRepository roleRepository,
                       PrivilegeRepository privilegeRepository,
                       MenuRepository menuRepository) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.menuRepository = menuRepository;
    }

    @Transactional
    public Role create(CreateRoleDTO dto) {
        Role role = new Role();
        role.setRoleName(dto.getRoleName());
        role.setRoleDesc(dto.getRoleDesc());

        // 处理权限关联
        if (dto.getPrivileges() != null && !dto.getPrivileges().isEmpty()) {
            Set<Privilege> privileges = new HashSet<>(
                privilegeRepository.findAllById(dto.getPrivileges())
            );
            role.setPrivileges(privileges);
        }

        // 处理菜单关联
        if (dto.getMenus() != null && !dto.getMenus().isEmpty()) {
            Set<Menu> menus = new HashSet<>(
                menuRepository.findAllById(dto.getMenus())
            );
            role.setMenus(menus);
        }

        return roleRepository.save(role);
    }

    public RoleListVO read(Map<String, Object> queryParams) {
        Integer page = (Integer) queryParams.getOrDefault("page", 1);
        Integer pageSize = (Integer) queryParams.getOrDefault("pageSize", 20);
        String roleName = (String) queryParams.get("roleName");

        Specification<Role> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (roleName != null && !roleName.isEmpty()) {
                predicates.add(cb.like(root.get("roleName"), "%" + roleName + "%"));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createTime").ascending());
        Page<Role> rolePage = roleRepository.findAll(spec, pageable);

        Pagination pagination = new Pagination(page, pageSize, rolePage.getTotalElements());
        
        return new RoleListVO(rolePage.getContent(), pagination);
    }

    public Role queryInfo(Long id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new BizException("角色不存在"));

        // 获取关联的权限和菜单，但只返回ID数组
        // 注意：这里需要特殊处理以返回ID数组形式
        return role;
    }

    @Transactional
    public Role update(Long id, UpdateRoleDTO dto) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new BizException("角色不存在"));

        if (dto.getRoleName() != null) {
            role.setRoleName(dto.getRoleName());
        }
        if (dto.getRoleDesc() != null) {
            role.setRoleDesc(dto.getRoleDesc());
        }

        // 更新权限关联
        if (dto.getPrivileges() != null) {
            Set<Privilege> privileges = new HashSet<>(
                privilegeRepository.findAllById(dto.getPrivileges())
            );
            role.setPrivileges(privileges);
        }

        // 更新菜单关联
        if (dto.getMenus() != null) {
            Set<Menu> menus = new HashSet<>(
                menuRepository.findAllById(dto.getMenus())
            );
            role.setMenus(menus);
        }

        return roleRepository.save(role);
    }

    @Transactional
    public boolean delete(Long id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new BizException("角色不存在"));
        
        roleRepository.delete(role);
        return true;
    }

    /**
     * 获取菜单权限树形数据
     */
    public List<MenuPrivilegeTreeNode> getMenuPrivilegeTree() {
        // 获取所有菜单
        List<Menu> menus = menuRepository.findAll(Sort.by("orderNum").ascending());
        
        // 获取所有权限
        List<Privilege> privileges = privilegeRepository.findAll();

        // 创建菜单Map，便于快速查找
        Map<String, MenuPrivilegeTreeNode> menuMap = new HashMap<>();
        
        for (Menu menu : menus) {
            MenuPrivilegeTreeNode node = new MenuPrivilegeTreeNode();
            node.setId(menu.getId());
            node.setLabel(menu.getMenuCnName() != null ? menu.getMenuCnName() : menu.getName());
            node.setValue(menu.getId());
            node.setType("menu");
            node.setPid(menu.getPid());
            node.setPath(menu.getPath());
            node.setName(menu.getName());
            node.setMenuCnName(menu.getMenuCnName());
            node.setOrderNum(menu.getOrderNum());
            node.setChildren(new ArrayList<>());
            
            menuMap.put(menu.getId(), node);
        }

        // 构建菜单树并添加权限节点
        List<MenuPrivilegeTreeNode> rootMenus = new ArrayList<>();
        
        for (Menu menu : menus) {
            MenuPrivilegeTreeNode currentMenu = menuMap.get(menu.getId());
            
            // 找到当前菜单相关的权限
            List<Privilege> menuPrivileges = privileges.stream()
                .filter(p -> menu.getId().equals(p.getPrivilegePage()))
                .collect(Collectors.toList());
            
            // 将权限作为子节点添加到菜单
            for (Privilege privilege : menuPrivileges) {
                MenuPrivilegeTreeNode privilegeNode = new MenuPrivilegeTreeNode();
                privilegeNode.setId(String.valueOf(privilege.getId()));
                privilegeNode.setLabel(privilege.getPrivilegeName());
                privilegeNode.setValue(String.valueOf(privilege.getId()));
                privilegeNode.setType("privilege");
                privilegeNode.setPrivilegeId(privilege.getId());
                privilegeNode.setPrivilegeName(privilege.getPrivilegeName());
                privilegeNode.setPrivilegeCode(privilege.getPrivilegeCode());
                privilegeNode.setPrivilegePage(privilege.getPrivilegePage());
                privilegeNode.setIsVisible(privilege.getIsVisible());
                
                currentMenu.getChildren().add(privilegeNode);
            }
            
            // 构建树形结构
            if ("0".equals(menu.getPid()) || menu.getPid() == null || menu.getPid().isEmpty()) {
                rootMenus.add(currentMenu);
            } else {
                MenuPrivilegeTreeNode parentMenu = menuMap.get(menu.getPid());
                if (parentMenu != null) {
                    // 将权限节点插入到菜单子节点后面
                    List<MenuPrivilegeTreeNode> childMenus = parentMenu.getChildren().stream()
                        .filter(child -> "menu".equals(child.getType()))
                        .collect(Collectors.toList());
                    childMenus.add(currentMenu);
                    
                    List<MenuPrivilegeTreeNode> childPrivileges = parentMenu.getChildren().stream()
                        .filter(child -> "privilege".equals(child.getType()))
                        .collect(Collectors.toList());
                    
                    parentMenu.getChildren().clear();
                    parentMenu.getChildren().addAll(childMenus);
                    parentMenu.getChildren().addAll(childPrivileges);
                }
            }
        }

        return rootMenus;
    }

    /**
     * 根据用户ID获取角色
     */
    public List<Role> getRoleByUserId(Long userId) {
        // 这个方法需要在后续实现用户管理时完善
        return new ArrayList<>();
    }
}
