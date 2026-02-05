package com.jiangxia.blog.admin.menu.repository;

import com.jiangxia.blog.admin.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, String> {
}
