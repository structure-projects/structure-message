---
name: structure-boot-new-controller
description: |
  当用户要求"新建 Controller/新增接口/写 API"时触发（structure-boot 栈）。
  按 RESTful 规范在 interfaces 模块创建 Controller。
  MUST 用 ResResultVO + ResultUtilSimpleImpl 统一响应。

triggers:
  - 新建 Controller
  - 新增接口
  - 写 API
  - 新增 Controller
  - new controller
  - add api
  - 新增 REST 接口

role: developer
phase: support
delegates-to: coding

when-to-use: |
  在 structure-boot 项目里新建 REST Controller。
when-not-to-use: |
  - 仅修改现有 Controller（用 coding）
  - 非 structure-boot 项目

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - structure-boot-developer
  - common-naming
  - common-api-design
  - common-project-stack-detection

reads-before-action:
  - wiki/structure-boot/developer.md
  - wiki/structure-boot/swagger.md
  - wiki/_common/api-design.md

stack-constraints:
  structure-boot:
    package: "cn.structured.{X}.interfaces.controller"
    naming: "{X}Controller / Open{X}Controller"
    inner-path: "/api/{resources}"
    open-path: "/api/open/{resources}"
    response: "ResResultVO<T> + ResultUtilSimpleImpl"
    forbidden:
      - "在 Controller 抛异常"
      - "在 Controller 注入 Mapper / Repository"
      - "直接返回 Entity / PO"

produces:
  - {X}Controller.java（内部 API）
  - Open{X}Controller.java（开放 API，可选）
  - 对应单元测试

requires:
  - skill: structure-boot-new-service
    condition: Service 层已存在
    error: 无 Service 层，MUST 先调用 structure-boot-new-service

human-in-the-loop:
  - API 路径 MUST 与用户确认

mode: auto

category: coding
stack: structure-boot
priority: high
maturity: stable
version: "0.3.0"
since: "2026-08-13"
---

# structure-boot 新建 Controller

> 在 interfaces 模块创建 REST Controller。**MUST 统一响应 + 异常处理**。

## 执行步骤

### 第 1 步：确认 Controller 类型

| 类型 | 路径 | 说明 |
|---|---|---|
| 内部 Controller | `controller/api/{X}Controller.java` | 前端 / 内部服务调用 |
| 开放 Controller | `controller/open/Open{X}Controller.java` | 第三方服务调用 |

### 第 2 步：生成内部 Controller

```java
package cn.structured.{X}.interfaces.controller.api;

import cn.structure.common.entity.ResResultVO;
import cn.structure.common.utils.ResultUtilSimpleImpl;
import cn.structure.common.vo.ResPage;
import cn.structure.common.vo.ReqPage;
import cn.structured.{X}.application.I{X}Service;
import cn.structured.{X}.common.dto.{X}DTO;
import cn.structured.{X}.common.query.{X}Query;
import cn.structured.{X}.common.vo.{X}VO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * {X}管理 Controller
 *
 * @author <author>
 * @version <version>
 * @since JDK 17 <date>
 */
@Tag(name = "{X}管理")
@RestController
@RequestMapping("/api/v1/{x}")
public class {X}Controller {

    @Resource
    private I{X}Service {x}Service;

    @Operation(summary = "根据 ID 查询{X}")
    @GetMapping("/{id}")
    public ResResultVO<{X}VO> findById(@PathVariable Long id) {
        return ResultUtilSimpleImpl.success({x}Service.findById(id));
    }

    @Operation(summary = "分页查询{X}")
    @GetMapping("/page")
    public ResResultVO<ResPage<{X}VO>> page({X}Query query, ReqPage reqPage) {
        return ResultUtilSimpleImpl.success({x}Service.page(query, reqPage));
    }

    @Operation(summary = "创建{X}")
    @PostMapping
    public ResResultVO<Long> create(@RequestBody @Valid {X}DTO dto) {
        return ResultUtilSimpleImpl.success({x}Service.create(dto));
    }

    @Operation(summary = "更新{X}")
    @PutMapping("/{id}")
    public ResResultVO<Void> update(@PathVariable Long id, @RequestBody @Valid {X}DTO dto) {
        {x}Service.update(dto);
        return ResultUtilSimpleImpl.success(null);
    }

    @Operation(summary = "删除{X}")
    @DeleteMapping("/{id}")
    public ResResultVO<Void> delete(@PathVariable Long id) {
        {x}Service.delete(id);
        return ResultUtilSimpleImpl.success(null);
    }
}
```

### 第 3 步：关键约束

| 约束 | 说明 |
|---|---|
| **包名** | `cn.structured.{X}.interfaces.controller.api` 或 `...open` |
| **命名** | `{X}Controller` / `Open{X}Controller` |
| **路径** | 内部 `/api/v1/{resources}`，开放 `/api/open/v1/{resources}` |
| **响应** | MUST `ResResultVO<T>` + `ResultUtilSimpleImpl` |
| **参数** | MUST `@Valid` 校验 |
| **禁止** | Controller 抛异常；注入 Mapper/Repository；直接返回 Entity/PO |

### 第 4 步：（可选）生成开放 Controller

```java
@Tag(name = "开放-{X}管理")
@RestController
@RequestMapping("/api/open/v1/{x}")
public class Open{X}Controller {
    // 类似，但路径前缀 /api/open/
}
```

## 产出物

- {X}Controller.java
- Open{X}Controller.java（可选）
- {X}ControllerTest.java

## 下一步

完成本技能后 MUST 按以下顺序继续：

1. **如还需配套组件** → 调用对应栈级 `new-*` 技能
2. **本层组件完成** → 调用 `unit-testing` 写测试
3. **全部代码完成** → 调用 `expert-review` 评审
4. **评审通过** → 调用 `ci-gate` 提交
5. **多人协作** → 调用 `gh-pr-workflow` 提 PR

**推荐下一技能**：`unit-testing`

## 关联

- 前置：`structure-boot-new-service`
- 相关：`structure-boot-new-repository`
- Wiki：`wiki/structure-boot/swagger.md`
