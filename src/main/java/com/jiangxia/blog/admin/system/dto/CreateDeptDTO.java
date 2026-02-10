package com.jiangxia.blog.admin.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建部门DTO")
public class CreateDeptDTO {

    @Schema(description = "部门名称", example = "技术部")
    @NotBlank(message = "请输入部门名称")
    @Size(min = 2, max = 50, message = "部门名称长度需要在2-50个字符之间")
    private String deptName;

    @Schema(description = "部门编码", example = "tech")
    @NotBlank(message = "请输入部门编码")
    @Size(min = 2, max = 50, message = "部门编码长度需要在2-50个字符之间")
    private String deptCode;

    @Schema(description = "父级部门ID", example = "0")
    private Long parentId = 0L;

    @Schema(description = "部门负责人ID")
    @Size(max = 50, message = "部门负责人ID不能超过50个字符")
    private String leaderId;

    @Schema(description = "部门负责人姓名")
    @Size(max = 50, message = "部门负责人姓名不能超过50个字符")
    private String leaderName;

    @Schema(description = "部门排序", example = "0")
    private Integer orderNum = 0;

    @Schema(description = "部门状态", example = "1")
    private Integer status = 1;

    @Schema(description = "部门描述")
    @Size(max = 200, message = "部门描述不能超过200个字符")
    private String remark;
}
