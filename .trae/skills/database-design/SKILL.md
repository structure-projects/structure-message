---
name: database-design
description: |
  当用户要求"设计表/加字段/写迁移/改表结构/设计索引"时触发。
  产出数据库表设计与 Flyway 迁移脚本。
  MUST 含审计字段、合适索引、逻辑删除。

triggers:
  - 设计表
  - 加字段
  - 写迁移
  - 改表结构
  - 设计索引
  - table
  - schema
  - migration
  - 索引
  - index
  - DDL

role: architect
phase: design
supports-skill: coding

when-to-use: |
  新建数据表、修改表结构、添加索引、编写迁移脚本。
when-not-to-use: |
  - 仅查询数据（用 coding）
  - 仅修改数据

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - common-database-design
  - common-naming
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/database-design.md
  - wiki/<stack>/orm-design.md

stack-constraints:
  structure-boot:
    migration-tool: "Flyway"
    migration-path: "db/migration/"
    migration-naming: "V{version}__{description}.sql"
    required-fields:
      - "id BIGINT PRIMARY KEY AUTO_INCREMENT"
      - "create_time DATETIME"
      - "update_time DATETIME ON UPDATE"
      - "deleted TINYINT DEFAULT 0"
      - "tenant_id BIGINT"
    forbidden:
      - "物理删除（MUST 逻辑删除 @TableLogic）"
      - "SELECT *"
      - "跨服务直接读库"

produces:
  - 数据表 DDL
  - Flyway 迁移脚本
  - 索引设计
  - 数据字典

requires:
  - skill: model-design
    condition: 模型设计已完成
    error: 无模型设计，MUST 先调用 model-design

human-in-the-loop:
  - 表名 / 字段名 MUST 与 DBA 确认
  - 索引设计 MUST 评估数据量

on-failure: |
  迁移脚本冲突 → 与用户确认版本号
  性能影响大 → 提供离线迁移方案

mode: assist

category: database-design
stack: _common
priority: high
version: "0.3.0"
since: "2026-08-13"
---

# 数据库设计

> 产出表结构与迁移脚本。**MUST 含审计字段、逻辑删除、合适索引**。

## 前置条件

- 模型设计已完成（`model-design` 技能产出）

## 执行步骤

### 第 1 步：设计表结构

```sql
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` BIGINT NOT NULL COMMENT '租户 ID',
  `username` VARCHAR(64) NOT NULL COMMENT '用户名',
  `password` VARCHAR(128) NOT NULL COMMENT '密码（加密存储）',
  `email` VARCHAR(128) COMMENT '邮箱',
  `mobile` VARCHAR(32) COMMENT '手机号',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1 启用 0 禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 正常 1 已删',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_username` (`tenant_id`, `username`),
  KEY `idx_email` (`email`),
  KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 第 2 步：关键约束（MUST 遵守）

| 约束 | 说明 |
|---|---|
| 主键 | `id BIGINT AUTO_INCREMENT`（不用 UUID，除非分库分表） |
| 审计字段 | `create_time` / `update_time` / `is_deleted` / `tenant_id` MUST 存在 |
| 逻辑删除 | `is_deleted TINYINT` + MyBatis-Plus `@TableLogic` |
| 命名 | 表名/字段名 MUST `lower_snake_case` |
| 字符集 | `utf8mb4` |
| 引擎 | `InnoDB` |
| 注释 | 每个表 / 字段 MUST 有 `COMMENT` |

### 第 3 步：设计索引

**MUST 索引**：
- 主键（PRIMARY KEY）
- 唯一约束（UNIQUE KEY）
- 高频查询字段（KEY）
- 外键关联字段（KEY）
- 多租户字段（tenant_id，几乎所有查询都带）

**禁止**：
- 在低选择性字段建索引（如 status 只有 0/1）
- 超过 5 个索引（影响写入性能）

### 第 4 步：生成 Flyway 迁移脚本

文件位置：`<stack>-repository-mybatis/src/main/resources/db/migration/`

命名规范：`V{version}__{description}.sql`

示例：`V1_2_0__add_user_table.sql`

```sql
-- V1_2_0__add_user_table.sql
-- 新增用户表

CREATE TABLE `user` (
  -- ... 上述 DDL
);
```

### 第 5 步：数据字典

写入 design.md：

```markdown
## 数据字典

### user 表

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| username | VARCHAR(64) | 用户名（租户内唯一） |
| ...
```

## 产出物

- 数据表 DDL
- Flyway 迁移脚本
- 索引设计
- 数据字典

## 完成标准

- 表结构含所有审计字段
- 索引设计合理
- 迁移脚本可执行（在测试库验证）
- 命名符合规范

## 关联

- 前置：`model-design`
- 后续：`coding`
- Wiki：`wiki/_common/database-design.md`
