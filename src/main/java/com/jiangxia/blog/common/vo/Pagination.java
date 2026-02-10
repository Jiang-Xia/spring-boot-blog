package com.jiangxia.blog.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {
    
    private Integer page;
    private Integer pageSize;
    private Long total;
    private Integer totalPages;

    public Pagination(Integer page, Integer pageSize, Long total) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }
}
