package com.jiangxia.blog.article.service;

import com.jiangxia.blog.article.dto.ArticleCreateDTO;
import com.jiangxia.blog.article.dto.ArticleEditDTO;
import com.jiangxia.blog.article.dto.ArticleListDTO;
import com.jiangxia.blog.article.entity.Article;
import com.jiangxia.blog.article.repository.ArticleRepository;
import com.jiangxia.blog.article.vo.ArticleDetailVO;
import com.jiangxia.blog.article.vo.ArticleItemVO;
import com.jiangxia.blog.article.vo.ArticleListVO;
import com.jiangxia.blog.category.entity.Category;
import com.jiangxia.blog.category.repository.CategoryRepository;
import com.jiangxia.blog.common.exception.BizException;
import com.jiangxia.blog.tag.entity.Tag;
import com.jiangxia.blog.tag.repository.TagRepository;
import com.jiangxia.blog.user.entity.User;
import com.jiangxia.blog.user.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public ArticleService(ArticleRepository articleRepository,
                          CategoryRepository categoryRepository,
                          TagRepository tagRepository,
                          UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    /**
     * 获取文章列表
     */
    public ArticleListVO getArticleList(ArticleListDTO dto, Long currentUserId) {
        // 构建查询条件
        Specification<Article> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 分类过滤
            if (dto.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), dto.getCategoryId()));
            }

            // 标签过滤
            if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
                predicates.add(root.join("tags").get("id").in(dto.getTagIds()));
            }

            // 客户端请求不返回已删除的
            if (Boolean.TRUE.equals(dto.getClient())) {
                predicates.add(cb.equal(root.get("isDelete"), false));
            }

            // 如果不是管理端且不是客户端，只返回当前用户的文章
            if (!Boolean.TRUE.equals(dto.getAdmin()) && !Boolean.TRUE.equals(dto.getClient())) {
                if (currentUserId != null) {
                    predicates.add(cb.equal(root.get("uid"), currentUserId));
                }
            }

            // 关键字搜索
            if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
                predicates.add(cb.like(root.get("title"), "%" + dto.getTitle() + "%"));
            }
            if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
                predicates.add(cb.like(root.get("description"), "%" + dto.getDescription() + "%"));
            }
            if (dto.getContent() != null && !dto.getContent().isEmpty()) {
                predicates.add(cb.like(root.get("content"), "%" + dto.getContent() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 排序：先置顶，再按创建时间
        Sort sort = "ASC".equalsIgnoreCase(dto.getSort())
                ? Sort.by(Sort.Order.desc("topping"), Sort.Order.asc("createTime"))
                : Sort.by(Sort.Order.desc("topping"), Sort.Order.desc("createTime"));

        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize(), sort);
        Page<Article> articlePage = articleRepository.findAll(spec, pageable);

        // 转换为VO
        List<ArticleItemVO> items = articlePage.getContent().stream()
                .map(this::convertToItemVO)
                .collect(Collectors.toList());

        // 构建分页信息
        ArticleListVO result = new ArticleListVO();
        result.setList(items);

        ArticleListVO.Pagination pagination = new ArticleListVO.Pagination();
        pagination.setPage(dto.getPage());
        pagination.setPageSize(dto.getPageSize());
        pagination.setTotal(articlePage.getTotalElements());
        pagination.setTotalPage(articlePage.getTotalPages());
        result.setPagination(pagination);

        return result;
    }

    /**
     * 获取文章详情
     */
    public ArticleDetailVO getArticleDetail(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BizException("文章不存在"));
        return convertToDetailVO(article);
    }

    /**
     * 根据ID或标题查询文章详情
     */
    public ArticleDetailVO getArticleByIdOrTitle(String idOrTitle) {
        Article article;
        try {
            Long id = Long.parseLong(idOrTitle);
            article = articleRepository.findByIdOrTitle(id, idOrTitle)
                    .orElseThrow(() -> new BizException("文章不存在"));
        } catch (NumberFormatException e) {
            article = articleRepository.findByTitle(idOrTitle)
                    .orElseThrow(() -> new BizException("文章不存在"));
        }
        return convertToDetailVO(article);
    }

    /**
     * 创建文章
     */
    @Transactional
    public Article createArticle(ArticleCreateDTO dto, Long userId) {
        // 检查标题是否已存在
        if (articleRepository.findByTitle(dto.getTitle()).isPresent()) {
            throw new BizException("文章标题已存在");
        }

        // 查询分类
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BizException("分类不存在"));

        // 查询标签
        Set<Tag> tags = new HashSet<>(tagRepository.findByIdIn(dto.getTagIds()));
        if (tags.size() != dto.getTagIds().size()) {
            throw new BizException("部分标签不存在");
        }

        // 查询用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        // 创建文章
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setDescription(dto.getDescription());
        article.setContent(dto.getContent());
        article.setContentHtml(dto.getContentHtml());
        article.setCover(dto.getCover());
        article.setCategory(category);
        article.setTags(tags);
        article.setUid(userId);
        article.setUser(user);

        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            // 这里可以添加状态验证
            article.setStatus(article.getStatus());
        }

        return articleRepository.save(article);
    }

    /**
     * 更新文章
     */
    @Transactional
    public Article updateArticle(ArticleEditDTO dto, Long userId) {
        Article article = articleRepository.findById(dto.getId())
                .orElseThrow(() -> new BizException("文章不存在"));

        // 权限检查：只能编辑自己的文章
        if (!article.getUid().equals(userId)) {
            throw new BizException("无权编辑此文章");
        }

        // 更新字段
        if (dto.getTitle() != null) {
            article.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            article.setDescription(dto.getDescription());
        }
        if (dto.getContent() != null) {
            article.setContent(dto.getContent());
        }
        if (dto.getContentHtml() != null) {
            article.setContentHtml(dto.getContentHtml());
        }
        if (dto.getCover() != null) {
            article.setCover(dto.getCover());
        }
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new BizException("分类不存在"));
            article.setCategory(category);
        }
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findByIdIn(dto.getTagIds()));
            article.setTags(tags);
        }
        if (dto.getIsDelete() != null) {
            article.setIsDelete(dto.getIsDelete());
        }

        return articleRepository.save(article);
    }

    /**
     * 删除文章
     */
    @Transactional
    public void deleteArticle(Long id, Long userId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BizException("文章不存在"));

        // 权限检查
        if (!article.getUid().equals(userId)) {
            throw new BizException("无权删除此文章");
        }

        articleRepository.delete(article);
    }

    /**
     * 更新文章访问量
     */
    @Transactional
    public void updateViews(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BizException("文章不存在"));
        article.setViews(article.getViews() + 1);
        articleRepository.save(article);
    }

    /**
     * 更新文章点赞数
     */
    @Transactional
    public void updateLikes(Long id, int status) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BizException("文章不存在"));
        if (status == 1) {
            article.setLikes(article.getLikes() + 1);
        } else {
            article.setLikes(Math.max(0, article.getLikes() - 1));
        }
        articleRepository.save(article);
    }

    /**
     * 更新文章字段（禁用/置顶）
     */
    @Transactional
    public Article updateArticleField(Long id, Boolean isDelete, Boolean topping) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BizException("文章不存在"));

        if (isDelete != null) {
            article.setIsDelete(isDelete);
        }
        if (topping != null) {
            article.setTopping(topping);
        }

        return articleRepository.save(article);
    }

    // 转换为列表项VO
    private ArticleItemVO convertToItemVO(Article article) {
        ArticleItemVO vo = new ArticleItemVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setDescription(article.getDescription());
        vo.setCover(article.getCover());
        vo.setLikes(article.getLikes());
        vo.setViews(article.getViews());
        vo.setCommentCount(0); // TODO: 需要关联评论表统计
        vo.setIsDelete(article.getIsDelete());
        vo.setTopping(article.getTopping());
        vo.setStatus(article.getStatus().toString().toLowerCase());
        vo.setCreateTime(article.getCreateTime());
        vo.setUTime(article.getUTime());

        // 分类信息
        if (article.getCategory() != null) {
            ArticleItemVO.CategoryVO categoryVO = new ArticleItemVO.CategoryVO();
            categoryVO.setId(article.getCategory().getId());
            categoryVO.setLabel(article.getCategory().getLabel());
            categoryVO.setValue(article.getCategory().getValue());
            categoryVO.setColor(article.getCategory().getColor());
            vo.setCategory(categoryVO);
        }

        // 标签信息
        if (article.getTags() != null) {
            List<ArticleItemVO.TagVO> tagVOs = article.getTags().stream().map(tag -> {
                ArticleItemVO.TagVO tagVO = new ArticleItemVO.TagVO();
                tagVO.setId(tag.getId());
                tagVO.setLabel(tag.getLabel());
                tagVO.setValue(tag.getValue());
                tagVO.setColor(tag.getColor());
                return tagVO;
            }).collect(Collectors.toList());
            vo.setTags(tagVOs);
        }

        // 用户信息
        if (article.getUser() != null) {
            ArticleItemVO.UserInfo userInfo = new ArticleItemVO.UserInfo();
            userInfo.setId(article.getUser().getId());
            userInfo.setNickname(article.getUser().getNickname());
            userInfo.setAvatar(article.getUser().getAvatar());
            vo.setUserInfo(userInfo);
        }

        return vo;
    }

    // 转换为详情VO
    private ArticleDetailVO convertToDetailVO(Article article) {
        ArticleDetailVO vo = new ArticleDetailVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setDescription(article.getDescription());
        vo.setCover(article.getCover());
        vo.setContent(article.getContent());
        vo.setContentHtml(article.getContentHtml());
        vo.setLikes(article.getLikes());
        vo.setViews(article.getViews());
        vo.setIsDelete(article.getIsDelete());
        vo.setTopping(article.getTopping());
        vo.setStatus(article.getStatus().toString().toLowerCase());
        vo.setCreateTime(article.getCreateTime());
        vo.setUpdateTime(article.getUpdateTime());
        vo.setUTime(article.getUTime());

        // 分类信息
        if (article.getCategory() != null) {
            ArticleDetailVO.CategoryVO categoryVO = new ArticleDetailVO.CategoryVO();
            categoryVO.setId(article.getCategory().getId());
            categoryVO.setLabel(article.getCategory().getLabel());
            categoryVO.setValue(article.getCategory().getValue());
            categoryVO.setColor(article.getCategory().getColor());
            vo.setCategory(categoryVO);
        }

        // 标签信息
        if (article.getTags() != null) {
            List<ArticleDetailVO.TagVO> tagVOs = article.getTags().stream().map(tag -> {
                ArticleDetailVO.TagVO tagVO = new ArticleDetailVO.TagVO();
                tagVO.setId(tag.getId());
                tagVO.setLabel(tag.getLabel());
                tagVO.setValue(tag.getValue());
                tagVO.setColor(tag.getColor());
                return tagVO;
            }).collect(Collectors.toList());
            vo.setTags(tagVOs);
        }

        // 用户信息
        if (article.getUser() != null) {
            ArticleDetailVO.UserInfo userInfo = new ArticleDetailVO.UserInfo();
            userInfo.setId(article.getUser().getId());
            userInfo.setNickname(article.getUser().getNickname());
            userInfo.setAvatar(article.getUser().getAvatar());
            vo.setUserInfo(userInfo);
        }

        return vo;
    }
}
