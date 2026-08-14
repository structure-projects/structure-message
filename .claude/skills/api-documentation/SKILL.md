---
name: api-documentation
description: |
  当用户要求"生成 API 文档/Swagger 文档/OpenAPI 文档"时触发。
  生成符合 OpenAPI 规范的 API 文档。

triggers:
  - 生成 API 文档
  - Swagger 文档
  - OpenAPI 文档
  - API 文档
  - api documentation

role: developer
phase: support

when-to-use: |
  需要生成或更新 API 文档。
when-not-to-use: |
  - 仅编写 API（用 api-design）
  - 仅查看现有文档

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - common-api-design
  - common-documentation

reads-before-action:
  - wiki/_common/api-design.md

produces:
  - OpenAPI / Swagger 文档
  - API 参考文档

requires: []

trust-level: standard

mode: auto

category: documentation
stack: _common
priority: medium
version: "0.3.0"
since: "2026-08-13"
---

# API 文档生成

> 生成 OpenAPI / Swagger 文档。

## 关键约束

- ✅ **MUST** 用 `springdoc-openapi`（Spring Boot 4）
- ✅ **MUST** 每个 Controller 有 `@Tag`
- ✅ **MUST** 每个方法有 `@Operation`
- ✅ **MUST** 每个参数有 `@Parameter`
- ❌ **MUST NOT** 用 swagger 2.x 老注解（`@Api` / `@ApiOperation`）

## 示例

```java
@Tag(name = "用户管理", description = "用户相关 API")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Operation(summary = "根据 ID 查询用户", description = "返回用户详情")
    @GetMapping("/{id}")
    public ResResultVO<UserVO> findById(
            @Parameter(description = "用户 ID", required = true)
            @PathVariable Long id) {
        return ResultUtilSimpleImpl.success(userService.findById(id));
    }
}
```

## 访问文档

```
http://localhost:8080/swagger-ui/index.html
```

## 关联

- Wiki：`wiki/_common/api-design.md`
- 相关：`api-design`
