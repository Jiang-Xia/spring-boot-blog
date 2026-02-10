package com.jiangxia.blog.admin.system.service;

import com.jiangxia.blog.admin.system.dto.CreateDeptDTO;
import com.jiangxia.blog.admin.system.dto.UpdateDeptDTO;
import com.jiangxia.blog.admin.system.entity.Dept;
import com.jiangxia.blog.admin.system.repository.DeptRepository;
import com.jiangxia.blog.admin.system.vo.DeptListVO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DeptService {

    private final DeptRepository deptRepository;

    public DeptService(DeptRepository deptRepository) {
        this.deptRepository = deptRepository;
    }

    @Transactional
    public Dept create(CreateDeptDTO dto) {
        Dept dept = new Dept();
        dept.setDeptName(dto.getDeptName());
        dept.setDeptCode(dto.getDeptCode());
        dept.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        dept.setLeaderId(dto.getLeaderId());
        dept.setLeaderName(dto.getLeaderName());
        dept.setOrderNum(dto.getOrderNum() != null ? dto.getOrderNum() : 0);
        dept.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        dept.setRemark(dto.getRemark());

        return deptRepository.save(dept);
    }

    public DeptListVO read(Map<String, Object> queryParams) {
        Integer page = (Integer) queryParams.getOrDefault("page", 1);
        Integer pageSize = (Integer) queryParams.getOrDefault("pageSize", 20);
        String deptName = (String) queryParams.get("deptName");
        Long parentId = queryParams.get("parentId") != null ? 
            Long.valueOf(queryParams.get("parentId").toString()) : null;

        Specification<Dept> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (deptName != null && !deptName.isEmpty()) {
                predicates.add(cb.like(root.get("deptName"), "%" + deptName + "%"));
            }
            
            if (parentId != null) {
                predicates.add(cb.equal(root.get("parentId"), parentId));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page - 1, pageSize, 
            Sort.by("orderNum").ascending().and(Sort.by("createTime").ascending()));
        Page<Dept> deptPage = deptRepository.findAll(spec, pageable);

        Pagination pagination = new Pagination(page, pageSize, deptPage.getTotalElements());
        
        return new DeptListVO(deptPage.getContent(), pagination);
    }

    public Dept queryInfo(Long id) {
        return deptRepository.findById(id)
            .orElseThrow(() -> new BizException("部门不存在"));
    }

    @Transactional
    public Dept update(Long id, UpdateDeptDTO dto) {
        Dept dept = deptRepository.findById(id)
            .orElseThrow(() -> new BizException("部门不存在"));

        if (dto.getDeptName() != null) {
            dept.setDeptName(dto.getDeptName());
        }
        if (dto.getDeptCode() != null) {
            dept.setDeptCode(dto.getDeptCode());
        }
        if (dto.getParentId() != null) {
            dept.setParentId(dto.getParentId());
        }
        if (dto.getLeaderId() != null) {
            dept.setLeaderId(dto.getLeaderId());
        }
        if (dto.getLeaderName() != null) {
            dept.setLeaderName(dto.getLeaderName());
        }
        if (dto.getOrderNum() != null) {
            dept.setOrderNum(dto.getOrderNum());
        }
        if (dto.getStatus() != null) {
            dept.setStatus(dto.getStatus());
        }
        if (dto.getRemark() != null) {
            dept.setRemark(dto.getRemark());
        }

        return deptRepository.save(dept);
    }

    @Transactional
    public boolean delete(Long id) {
        Dept dept = deptRepository.findById(id)
            .orElseThrow(() -> new BizException("部门不存在"));
        
        // 检查是否有子部门
        List<Dept> childDepts = deptRepository.findByParentId(id);
        if (!childDepts.isEmpty()) {
            throw new BizException("该部门下存在子部门，无法删除");
        }
        
        deptRepository.delete(dept);
        return true;
    }

    /**
     * 查询部门树形结构
     */
    public List<Dept> queryTree(Long parentId) {
        // 查询所有部门
        List<Dept> allDepts = deptRepository.findAll(
            Sort.by("orderNum").ascending().and(Sort.by("createTime").ascending())
        );

        // 创建部门Map，便于快速查找
        Map<Long, Dept> deptMap = new HashMap<>();
        for (Dept dept : allDepts) {
            dept.setChildren(new ArrayList<>());
            deptMap.put(dept.getId(), dept);
        }

        // 构建树形结构
        for (Dept dept : allDepts) {
            if (dept.getParentId() != null && dept.getParentId() != 0) {
                Dept parentDept = deptMap.get(dept.getParentId());
                if (parentDept != null) {
                    parentDept.getChildren().add(dept);
                }
            }
        }

        // 如果指定了parentId，返回该节点及其子树
        if (parentId != null) {
            Dept targetDept = deptMap.get(parentId);
            return targetDept != null ? List.of(targetDept) : new ArrayList<>();
        }

        // 否则返回所有根节点
        return allDepts.stream()
            .filter(dept -> dept.getParentId() == null || dept.getParentId() == 0)
            .collect(Collectors.toList());
    }
}
