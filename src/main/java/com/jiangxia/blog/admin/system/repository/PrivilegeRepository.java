package com.jiangxia.blog.admin.system.repository;

import com.jiangxia.blog.admin.system.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    @Query("select p from Privilege p join p.roles r where r.id = :roleId")
    List<Privilege> findByRoleId(@Param("roleId") Long roleId);
}
