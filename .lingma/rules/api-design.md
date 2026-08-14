---
description: |
triggers:
  - 设计接口
  - 定 API
  - 写 OpenAPI
  - 定义接口
  - API 设计
  - api
  - interface
  - endpoint
  - OpenAPI
  - RESTful
role: architect
priority: high
category: api-design
stack: _common
alwaysApply: false
---


# API 设计

> 按 RESTful 规范设计 API 契约。**MUST 含版本号、幂等性、错误码、分页约定**。

## 前置条件

- 变更提案存在
- 已识别项目栈

## 执行步骤

### 第 1 步：确定 API 类型

| 类型 | 前缀 | 说明 |
|---|---|---|
| **内部 API** | `/api/{资源}` | 前端 / 内部服务调用，需认证 |
| **开放 API** | `/api/open/{资源}` | 第三方服务调用，需签名 / 开放认证 |

### 第 2 步：设计 API 路径（MUST 遵守）

- MUST `kebab-case`：`/api/user-roles`（不是 `/api/userRoles`）
- MUST 用名词复数：`/users`（不是 `/user`）
- MUST 含版本号：`/api/v1/users`
- MUST NOT 含动词：`/api/users`（不是 `/api/getUsers`）

### 第 3 步：设计 HTTP 方法

| 操作 | 方法 | 路径示例 | 幂等 |
|---|---|---|---|
| 查询单条 | GET | `/api/v1/users/{id}` | ✅ |
| 查询列表 | GET | `/api/v1/users` | ✅ |
| 分页查询 | GET | `/api/v1/users/page` | ✅ |
| 创建 | POST | `/api/v1/users` | ❌（需幂等键） |
| 全量更新 | PUT | `/api/v1/users/{id}` | ✅ |
| 部分更新 | PATCH | `/api/v1/users/{id}` | ❌ |
| 删除 | DELETE | `/api/v1/users/{id}` | ✅ |

### 第 4 步：设计请求/响应

#### 请求

- 路径参数：`@PathVariable Long id`
- 查询参数：`@RequestParam String username`
- 请求体：`@RequestBody @Valid UserDTO`
- 分页：`page(UserQuery query, ReqPage reqPage)`

#### 响应

```java
// 统一响应包装
ResResultVO<UserVO>

// 分页响应
ResResultVO<ResPage<UserVO>>

// 构造
ResultUtilSimpleImpl.success(data)
ResultUtilSimpleImpl.fail(code, message)
```

### 第 5 步：设计错误码

```java
public enum UserExceptionEnum {
    USER_NOT_FOUND("USER_001", "用户不存在"),
    USERNAME_DUPLICATED("USER_002", "用户名已存在"),
    ...
}
```

**规则**：
- 错误码格式：`{MODULE}_{3 位数字}`
- MUST 在 `{X}ExceptionEnum` 集中管理
- MUST 抛 `CommonException`

### 第 6 步：幂等性设计

非幂等操作（POST / PATCH）MUST 支持幂等：
- 客户端传 `Idempotency-Key` header
- 服务端去重（Redis SETNX）

### 第 7 步：生成 OpenAPI 注解

```java
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Operation(summary = "根据 ID 查询用户")
    @GetMapping("/{id}")
    public ResResultVO<UserVO> findById(@PathVariable Long id) {
        return ResultUtilSimpleImpl.success(userService.findById(id));
    }
}
```

## 产出物

- API 契约文档
- Controller 接口骨架
- DTO / VO 定义
- 错误码枚举
- OpenAPI / Swagger 注解

## 完成标准

- 路径符合 RESTful
- 响应统一 `ResResultVO<T>`
- 错误码集中管理
- 非幂等操作支持幂等键
- OpenAPI 注解完整

## 关联

- 前置：`requirement-analysis`
- 后续：`coding`
- Wiki：`wiki/_common/api-design.md` `wiki/<stack>/swagger.md`
