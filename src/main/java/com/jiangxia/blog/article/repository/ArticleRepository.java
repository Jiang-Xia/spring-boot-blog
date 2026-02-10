package com.jiangxia.blog.article.repository;

import com.jiangxia.blog.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    Optional<Article> findByTitle(String title);

    Optional<Article> findByIdOrTitle(Long id, String title);
}
