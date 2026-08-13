---
name: structure-boot-new-entity
description: |
  当用户要求"新建实体/新增 Entity/新增 PO/新增 DTO/新增 VO"时触发（structure-boot 栈）。
  按 DDD 规范创建四层模型（Entity / PO / DTO / VO / Query）。

triggers:
  - 新建实体
  - 新增 Entity
  - 新增 PO
  - 新增 DTO
  - 新增 VO
  - new entity
  - add entity

role: developer
phase: support
delegates-to: model-design

when-to-use: |
  在 structure-boot 项目里新建四层模型类。
when-not-to-use: |
  - 仅修改现有模型字段（用 coding）
  - 非 structure-boot 项目

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - structure-boot-developer
  - common-naming
  - common-model-design
  - common-project-stack-detection

reads-before-action:
  - wiki/structure-boot/developer.md
  - wiki/_common/model-design.md
  - wiki/_common/naming.md

stack-constraints:
  structure-boot:
    entity-package: "cn.structured.{X}.domain.entity"
    po-package: "cn.structured.{X}.repository.po"
    dto-package: "cn.structured.{X}.common.dto"
    vo-package: "cn.structured.{X}.common.vo"
    query-package: "cn.structured.{X}.common.query"
    naming:
      entity: "{X}Entity"
      po: "{X}PO"
      dto: "{X}DTO"
      vo: "{X}VO"
      query: "{X}Query"
    required-annotations:
      po: ["@TableName", "@TableId", "@TableLogic"]
      dto: ["@Valid 校验"]
    forbidden:
      - "Entity 上加 @TableId / @TableLogic"
      - "在 Service 直接返回 PO"

produces:
  - {X}Entity.java
  - {X}PO.java
  - {X}DTO.java
  - {X}VO.java
  - {X}Query.java

requires:
  - skill: model-design
    condition: 模型设计已完成
    error: 无模型设计，MUST 先调用 model-design

mode: auto

category: coding
stack: structure-boot
priority: high
maturity: stable
version: "0.3.0"
since: "2026-08-13"
---

# structure-boot 新建 Entity / PO / DTO / VO / Query

> 按 DDD 四层模型规范创建。**MUST 区分五层模型的位置与职责**。

## 执行步骤

### 第 1 步：Entity（domain 模块）

```java
package cn.structured.{X}.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * {X}领域实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {X}Entity {

    private Long id;
    private Long tenantId;
    // ... 业务字段
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
}
```

**约束**：
- MUST `@Data` + `@Builder` + `@NoArgsConstructor` + `@AllArgsConstructor`
- MUST NOT 加 `@TableId` / `@TableLogic` 等持久化注解
- MUST 含审计字段

### 第 2 步：PO（repository-mybatis 模块）

```java
package cn.structured.{X}.repository.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * {X}持久化对象
 */
@Data
@TableName("{x}")
public class {X}PO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    // ... 业务字段
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer isDeleted;
}
```

**约束**：
- MUST `@TableName("{x}")`
- MUST `@TableId(type = IdType.AUTO)`
- MUST `@TableLogic` 在 deleted 字段

### 第 3 步：DTO / VO / Query（common 模块）

```java
// DTO：服务间传输
package cn.structured.{X}.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class {X}DTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    // ...
}

// VO：视图对象
package cn.structured.{X}.common.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class {X}VO {
    private Long id;
    private String username;
    private LocalDateTime createdAt;
    // ...
}

// Query：查询对象
package cn.structured.{X}.common.query;

import lombok.Data;

@Data
public class {X}Query {
    private String username;
    private Integer status;
    // ...
}
```

## 产出物

- {X}Entity.java
- {X}PO.java
- {X}DTO.java
- {X}VO.java
- {X}Query.java

## 下一步

完成本技能后 MUST 按以下顺序继续：

1. **如还需配套组件** → 调用对应栈级 `new-*` 技能
2. **本层组件完成** → 调用 `unit-testing` 写测试
3. **全部代码完成** → 调用 `expert-review` 评审
4. **评审通过** → 调用 `ci-gate` 提交
5. **多人协作** → 调用 `gh-pr-workflow` 提 PR

**推荐下一技能**：`structure-boot-new-repository`

## 关联

- 前置：`model-design`
- 后续：`structure-boot-new-repository`
- Wiki：`wiki/structure-boot/developer.md` `wiki/_common/model-design.md`
