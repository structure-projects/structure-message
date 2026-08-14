# 安全集成 — structure-projects 认证授权约束

> 本文是安全集成的 **速查手册**，覆盖 JWT、OAuth2、用户上下文、权限注解与包名陷阱。
> 组件级 API 详见 [`components.md`](components.md) 第 2 节；通用安全基线见 `wiki/_common/security.md`。

## JWT 认证流程

`structure-security-jwt-starter` 提供 JWT 认证能力，配置类 `cn.structure.starter.jwt.properties.JwtConfig`（**无 d**，注意包名陷阱）。

```yaml
structure:
  jwt:
    secret: your-secret-key          # 默认 "JWT"（生产 MUST 改）
    jwt-token-validity: 32400        # Token 有效期（秒），默认 32400 = 9 小时
```

**认证流程**：
1. 客户端携带 `Authorization: Bearer {token}` 请求。
2. `structure-gateway` 的 `TokenVerificationFilter` 仅校验 Token **存在性**（不验真伪）。
3. 下游服务自行解析 JWT 或调用 auth-center 获取用户身份。
4. 认证成功后写入 `UserContext`（ThreadLocal）。

- **MUST** 生产环境 `secret` 改为强随机值，**禁止**用默认 `"JWT"`。
- **MUST** 下游服务用户身份 MUST 自行解析 JWT，**禁止**假设 `X-User-Id` Header 存在（gateway 不下发）。

## OAuth2 集成

| 模块 | 用途 |
|---|---|
| `structure-security-oauth-common` | OAuth2 公共组件 |
| `structure-security-oauth-sdk` | OAuth2 SDK |
| `structure-security-oauth-resource-starter` | OAuth2 资源服务器 |

- **SHOULD** 对外暴露的 API 优先用 OAuth2 资源服务器模式；内部服务间调用用 JWT。
- **MUST** 微服务间调用通过 Feign 自动传递 Token（OAuth2 Token 自动管理）。

## UserContext 静态方法（规则 13）

**业务侧主入口**：`cn.structured.security.context.UserContext`（静态 ThreadLocal 工具类，位于 `structure-security-core`，**无需注入**）。

| 方法 | 返回 | 用途 |
|---|---|---|
| `UserContext.get()` | `UserContextEntity`（可空） | 获取完整用户实体 |
| `UserContext.getUserId()` | `String` | 用户 ID（String） |
| `UserContext.getLongUserId()` | `Long` | 用户 ID（**推荐**，免手写 parseLong） |
| `UserContext.getLongDeptId()` | `Long` | 部门 ID |
| `UserContext.getLoneDeptIds()` | `Set<Long>` | 部门 ID 集合（⚠️ 拼写 bug，应为 `getLongDeptIds`） |
| `UserContext.getLongRoles()` | `Set<Long>` | 角色集合 |
| `UserContext.getLongPermissions()` | `Set<Long>` | 权限集合 |
| `UserContext.set(info)` | — | 写入上下文（框架/认证侧用） |
| `UserContext.remove()` | — | 清理上下文（**MUST 请求结束调用**） |

```java
// ✅ 推荐：直接用静态便捷方法
Long userId = UserContext.getLongUserId();
if (userId == null) {
    throw new OrderException(OrderExceptionEnum.NOT_LOGGED_IN);
}

// ❌ 避免：手写判空 + parseLong
UserContextEntity e = UserContext.get();
if (e != null) { return Long.parseLong(e.getUserId()); }
```

- **MUST** 非控制层（Service / Domain / Infra / Assembler / 异步任务）通过 `UserContext` 静态方法获取当前用户。
- **禁止** 非控制层使用 `SecurityUtils` / `SecurityContextHolder` —— 非 HTTP 入口（消息消费、定时任务、内部 RPC）无法获取。
- **SHOULD** 优先用 `getLongUserId()` 等 Long 型方法，避免手写 `Long.parseLong(...)`。
- ⚠️ **已知拼写 bug**：`getLoneDeptIds()` 应为 `getLongDeptIds()`，新代码注意。

## SecurityUtils 工具类

| 类 | 包 | 用法约束 |
|---|---|---|
| `SecurityUtils` | `cn.structured.security.util` | **仅控制层可用**；非控制层 MUST 用 `UserContext` |

- **控制层**：`SecurityUtils` 或 `UserContext` 均可。
- **非控制层**：**MUST** 用 `UserContext` 静态方法。

## 权限注解（@RequiresPermission / @RequiresRole）

| 注解 | 包 | 级别 | 用途 |
|---|---|---|---|
| `@RequiresPermission("order:create")` | `cn.structured.starter.permission.annotations` | METHOD | 编程式权限校验 |
| `@RequiresRole("admin")` | `cn.structured.starter.permission.annotations` | METHOD | 角色校验 |

```java
@RequiresPermission("order:create")
public void createOrder(OrderDTO dto) { ... }

@RequiresPermission("system:user:read")
public User getUser(Long id) { ... }
```

- **MUST** 敏感接口加权限注解（`@RequiresPermission` / `@RequiresRole`），或用 `@PreAuthorize("hasAuthority('xxx')")`。
- **MUST** 权限字符串格式：冒号分层 `"order:create"` / `"system:user:read"`，支持通配符匹配。
- **编程式检查**：注入 `IPermissionService`（`cn.structured.starter.permission.service`）调 `hasPermission(perm)` / `getUserPermissions()`。

## 包名不一致案例（MUST 核对）

`structure-security` 内部不同 starter 包名 **不统一**，生成 import 前 MUST 核对：

| Starter | 实际包名 | 有无 d |
|---|---|---|
| `jwt-starter` | `cn.structure.starter.jwt.*` | **无 d** ⚠️ |
| `permission-starter` | `cn.structured.starter.permission.*` | 有 d |
| `context-starter` | `cn.structured.starter.context.*` | 有 d |
| `structure-security`（含 `UserContext`） | `cn.structured.security.*` | 有 d |

- ⚠️ `UserContext` 在 `cn.structured.security.context.*`（`structure-security-core`），**不是** `cn.structured.starter.context.*`（那是底层 SPI `IContextManager`）。
- **MUST** 按目标类所在的具体 starter 核对包名，**禁止**默认所有 starter 都是 `cn.structured.starter.*`。

## 会话管理

| 接口 | 包 | 用途 |
|---|---|---|
| `IContextManager` | `cn.structured.starter.context.manager` | 认证侧 SPI：`login` / `logout` / `updateUser` / `getUser` |
| `IUserStore` | `cn.structured.starter.context.store` | 用户存储：`DefaultUserStore`（内存）/ `RedisUserStore`（预留） |

- **MUST** `UserContext.remove()` 在请求/任务结束调用，防 ThreadLocal 泄漏。
- **SHOULD** 会话存储用 `RedisUserStore`（集群场景），**禁止**单体内存存储用于生产集群。

## Token 刷新策略

- **MUST** Token 过期由前端 `@structure-projects/gateway-client` 检测响应码 `INVALID_AUTHENTICATION` / `NOT_LOGGED_IN` 触发刷新。
- **MUST** 并发请求时通过 `failedQueue` 排队等新 Token 后重放原请求（`_retry` 防循环）。
- **SHOULD** 刷新 Token 走独立端点，**禁止**用业务接口复用刷新逻辑。

## 与其他规则的关系

- 用户上下文完整 API：[`components.md`](components.md) 第 2 节
- 通用安全基线（OWASP）：`wiki/_common/security.md`
- 数据权限（行级/列级）：[`data-permission.md`](data-permission.md)
- 网关 Filter 链与 Header：[`components.md`](components.md) 第 5 节
