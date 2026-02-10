package com.jiangxia.blog.admin.system.repository;

import com.jiangxia.blog.admin.system.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long>, JpaSpecificationExecutor<Privilege> {

    @Query("select p from Privilege p join p.roles r where r.id = :roleId")
    List<Privilege> findByRoleId(@Param("roleId") Long roleId);
}
