package com.jiangxia.blog.admin.system.vo;

import com.jiangxia.blog.admin.system.entity.Dept;
import com.jiangxia.blog.common.vo.Pagination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptListVO {
    
    private List<Dept> list;
    private Pagination pagination;
}
