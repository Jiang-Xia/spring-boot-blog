package com.jiangxia.blog.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "用户列表查询DTO")
@Getter
@Setter
public class UserListQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", example = "20")
    private Integer pageSize = 20;

    @Schema(description = "手机号", example = "13049153466")
    private String mobile;

    @Schema(description = "用户名", example = "jiangxia")
    private String username;
}
