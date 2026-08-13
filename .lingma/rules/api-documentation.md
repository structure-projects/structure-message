---
description: |
triggers:
  - 生成 API 文档
  - Swagger 文档
  - OpenAPI 文档
  - API 文档
  - api documentation
role: developer
priority: medium
category: documentation
stack: _common
alwaysApply: false
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
