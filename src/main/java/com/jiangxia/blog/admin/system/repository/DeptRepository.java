package com.jiangxia.blog.admin.system.repository;

import com.jiangxia.blog.admin.system.entity.Dept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptRepository extends JpaRepository<Dept, Long>, JpaSpecificationExecutor<Dept> {
    
    List<Dept> findByParentId(Long parentId);
}
