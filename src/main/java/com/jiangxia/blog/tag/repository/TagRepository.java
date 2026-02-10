package com.jiangxia.blog.tag.repository;

import com.jiangxia.blog.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByIdIn(List<Long> ids);
}
