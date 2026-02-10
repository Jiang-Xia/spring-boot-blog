package com.jiangxia.blog.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "用户列表响应VO")
@Getter
@Setter
public class UserListVO {

    @Schema(description = "用户列表")
    private List<UserInfoVO> list;

    @Schema(description = "分页信息")
    private Pagination pagination;

    public UserListVO() {
    }

    public UserListVO(List<UserInfoVO> list, Pagination pagination) {
        this.list = list;
        this.pagination = pagination;
    }

    // Getters and Setters
    @Getter
    @Setter
    @Schema(description = "分页信息")
    public static class Pagination {
        @Schema(description = "总数")
        private Long total;

        @Schema(description = "当前页")
        private Integer page;

        @Schema(description = "每页数量")
        private Integer pageSize;

        @Schema(description = "总页数")
        private Integer totalPages;

        public Pagination() {
        }

        public Pagination(Long total, Integer page, Integer pageSize) {
            this.total = total;
            this.page = page;
            this.pageSize = pageSize;
            this.totalPages = (int) Math.ceil((double) total / pageSize);
        }
    }
}
