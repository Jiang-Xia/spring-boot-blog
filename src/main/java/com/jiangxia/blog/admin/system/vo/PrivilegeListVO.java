package com.jiangxia.blog.admin.system.vo;

import com.jiangxia.blog.admin.system.entity.Privilege;
import com.jiangxia.blog.common.vo.Pagination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegeListVO {
    
    private List<Privilege> list;
    private Pagination pagination;
}
