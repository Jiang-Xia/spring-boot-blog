package com.jiangxia.blog.admin.system.service;

import com.jiangxia.blog.admin.system.dto.CreatePrivilegeDTO;
import com.jiangxia.blog.admin.system.dto.UpdatePrivilegeDTO;
import com.jiangxia.blog.admin.system.entity.Privilege;
import com.jiangxia.blog.admin.system.repository.PrivilegeRepository;
import com.jiangxia.blog.admin.system.vo.PrivilegeListVO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    public PrivilegeService(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    @Transactional
    public Privilege create(CreatePrivilegeDTO dto) {
        Privilege privilege = new Privilege();
        privilege.setPrivilegeName(dto.getPrivilegeName());
        privilege.setPrivilegeCode(dto.getPrivilegeCode());
        privilege.setPrivilegePage(dto.getPrivilegePage());
        privilege.setIsVisible(dto.getIsVisible());
        privilege.setPathPattern(dto.getPathPattern());
        privilege.setHttpMethod(dto.getHttpMethod());
        privilege.setIsPublic(dto.getIsPublic());
        privilege.setRequireOwnership(dto.getRequireOwnership());
        privilege.setDescription(dto.getDescription());

        return privilegeRepository.save(privilege);
    }

    public PrivilegeListVO read(Map<String, Object> queryParams) {
        Integer page = (Integer) queryParams.getOrDefault("page", 1);
        Integer pageSize = (Integer) queryParams.getOrDefault("pageSize", 20);
        String privilegeName = (String) queryParams.get("privilegeName");

        Specification<Privilege> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (privilegeName != null && !privilegeName.isEmpty()) {
                predicates.add(cb.like(root.get("privilegeName"), "%" + privilegeName + "%"));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createTime").ascending());
        Page<Privilege> privilegePage = privilegeRepository.findAll(spec, pageable);

        Pagination pagination = new Pagination(page, pageSize, privilegePage.getTotalElements());
        
        return new PrivilegeListVO(privilegePage.getContent(), pagination);
    }

    public Privilege queryInfo(Long id) {
        return privilegeRepository.findById(id)
            .orElseThrow(() -> new BizException("权限不存在"));
    }

    @Transactional
    public Privilege update(Long id, UpdatePrivilegeDTO dto) {
        Privilege privilege = privilegeRepository.findById(id)
            .orElseThrow(() -> new BizException("权限不存在"));

        if (dto.getPrivilegeName() != null) {
            privilege.setPrivilegeName(dto.getPrivilegeName());
        }
        if (dto.getPrivilegeCode() != null) {
            privilege.setPrivilegeCode(dto.getPrivilegeCode());
        }
        if (dto.getPrivilegePage() != null) {
            privilege.setPrivilegePage(dto.getPrivilegePage());
        }
        if (dto.getIsVisible() != null) {
            privilege.setIsVisible(dto.getIsVisible());
        }
        if (dto.getPathPattern() != null) {
            privilege.setPathPattern(dto.getPathPattern());
        }
        if (dto.getHttpMethod() != null) {
            privilege.setHttpMethod(dto.getHttpMethod());
        }
        if (dto.getIsPublic() != null) {
            privilege.setIsPublic(dto.getIsPublic());
        }
        if (dto.getRequireOwnership() != null) {
            privilege.setRequireOwnership(dto.getRequireOwnership());
        }
        if (dto.getDescription() != null) {
            privilege.setDescription(dto.getDescription());
        }

        return privilegeRepository.save(privilege);
    }

    @Transactional
    public boolean delete(Long id) {
        Privilege privilege = privilegeRepository.findById(id)
            .orElseThrow(() -> new BizException("权限不存在"));
        
        privilegeRepository.delete(privilege);
        return true;
    }

    /**
     * 根据角色ID查询权限
     */
    public List<Privilege> queryByRoleId(Long roleId) {
        return privilegeRepository.findByRoleId(roleId);
    }
}
