-- ========================================================
-- Spring Boot Blog - 角色权限系统数据库脚本
-- 生成时间：2026-02-06
-- 说明：请在执行前确认数据库连接，手动执行此脚本
-- ========================================================

-- 1. 删除已存在的表（谨慎操作，会删除数据！）
-- DROP TABLE IF EXISTS `role_users_user`;
-- DROP TABLE IF EXISTS `role_menus_menu`;
-- DROP TABLE IF EXISTS `role_privileges_privilege`;
-- DROP TABLE IF EXISTS `privilege`;
-- DROP TABLE IF EXISTS `dept`;
-- DROP TABLE IF EXISTS `role`;

-- 2. 创建角色表
CREATE TABLE IF NOT EXISTS `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `roleName` VARCHAR(50) NOT NULL COMMENT '角色名',
  `roleDesc` VARCHAR(200) COMMENT '角色描述',
  PRIMARY KEY (`id`),
  KEY `idx_role_name` (`roleName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- 3. 创建权限表
CREATE TABLE IF NOT EXISTS `privilege` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `privilegeName` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `privilegeCode` VARCHAR(100) NOT NULL COMMENT '权限识别码',
  `privilegePage` VARCHAR(100) NOT NULL COMMENT '所属页面(菜单id)',
  `isVisible` TINYINT NOT NULL DEFAULT 1 COMMENT '是否可见',
  `pathPattern` VARCHAR(500) NOT NULL COMMENT '路径模式，如 /api/users/:id',
  `httpMethod` VARCHAR(10) NOT NULL COMMENT 'HTTP方法，*表示全部',
  `isPublic` TINYINT NOT NULL DEFAULT 0 COMMENT '是否公开接口',
  `requireOwnership` TINYINT NOT NULL DEFAULT 0 COMMENT '是否需要检查资源所有权',
  `description` VARCHAR(500) COMMENT '描述',
  PRIMARY KEY (`id`),
  KEY `idx_privilege_code` (`privilegeCode`),
  KEY `idx_privilege_page` (`privilegePage`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';

-- 4. 创建部门表
CREATE TABLE IF NOT EXISTS `dept` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deptName` VARCHAR(50) NOT NULL COMMENT '部门名称',
  `deptCode` VARCHAR(50) NOT NULL COMMENT '部门编码',
  `parentId` BIGINT NOT NULL DEFAULT 0 COMMENT '父级部门ID，顶级部门为0',
  `leaderId` VARCHAR(50) COMMENT '部门负责人ID',
  `leaderName` VARCHAR(50) COMMENT '部门负责人姓名',
  `orderNum` INT NOT NULL DEFAULT 0 COMMENT '部门排序',
  `status` INT NOT NULL DEFAULT 1 COMMENT '部门状态 1-正常 0-禁用',
  `remark` VARCHAR(200) COMMENT '部门描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dept_code` (`deptCode`),
  KEY `idx_parent_id` (`parentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';

-- 5. 创建角色-权限关联表
CREATE TABLE IF NOT EXISTS `role_privileges_privilege` (
  `roleId` BIGINT NOT NULL COMMENT '角色ID',
  `privilegeId` BIGINT NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`roleId`, `privilegeId`),
  KEY `idx_role_id` (`roleId`),
  KEY `idx_privilege_id` (`privilegeId`),
  CONSTRAINT `fk_rpp_role` FOREIGN KEY (`roleId`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_rpp_privilege` FOREIGN KEY (`privilegeId`) REFERENCES `privilege` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关联表';

-- 6. 创建角色-菜单关联表
-- 注意：此表依赖已存在的 menu 表，如果 menu 表不存在，请先创建
CREATE TABLE IF NOT EXISTS `role_menus_menu` (
  `roleId` BIGINT NOT NULL COMMENT '角色ID',
  `menuId` VARCHAR(255) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`roleId`, `menuId`),
  KEY `idx_role_id` (`roleId`),
  KEY `idx_menu_id` (`menuId`),
  CONSTRAINT `fk_rmm_role` FOREIGN KEY (`roleId`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_rmm_menu` FOREIGN KEY (`menuId`) REFERENCES `menu` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单关联表';

-- 7. 创建用户-角色关联表
-- 注意：此表依赖已存在的 user 表，如果 user 表不存在，请先创建
CREATE TABLE IF NOT EXISTS `role_users_user` (
  `userId` BIGINT NOT NULL COMMENT '用户ID',
  `roleId` BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`userId`, `roleId`),
  KEY `idx_user_id` (`userId`),
  KEY `idx_role_id` (`roleId`),
  CONSTRAINT `fk_ruu_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ruu_role` FOREIGN KEY (`roleId`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- ========================================================
-- 初始化数据（可选）
-- ========================================================

-- 插入默认角色
INSERT INTO `role` (`roleName`, `roleDesc`) VALUES
('超级管理员', '拥有系统所有权限'),
('管理员', '拥有部分管理权限'),
('普通用户', '普通用户权限');

-- 插入默认部门
INSERT INTO `dept` (`deptName`, `deptCode`, `parentId`, `orderNum`, `status`, `remark`) VALUES
('总公司', 'head_office', 0, 1, 1, '总公司'),
('技术部', 'tech_dept', 1, 1, 1, '技术部门'),
('运营部', 'ops_dept', 1, 2, 1, '运营部门');

-- ========================================================
-- 验证脚本
-- ========================================================

-- 查看表结构
-- SHOW CREATE TABLE `role`;
-- SHOW CREATE TABLE `privilege`;
-- SHOW CREATE TABLE `dept`;

-- 查看已创建的表
-- SHOW TABLES LIKE '%role%';
-- SHOW TABLES LIKE '%privilege%';
-- SHOW TABLES LIKE '%dept%';

-- 查看数据
-- SELECT * FROM `role`;
-- SELECT * FROM `dept`;

-- ========================================================
-- 注意事项
-- ========================================================
-- 1. 执行前请备份数据库
-- 2. 确认 menu 和 user 表已存在
-- 3. 根据实际情况调整字符集和排序规则
-- 4. 如果需要删除表重新创建，请先删除关联表，再删除主表
-- 5. 外键约束会自动级联删除关联数据，请谨慎操作
