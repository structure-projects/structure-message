# 多租户设计 — structure-projects 多租户约束

> 本文是多租户场景的 **速查手册**，覆盖上下文获取、插件配置、隔离级别与跨租户限制。
> 组件级配置见 [`components.md`](components.md) `structure-tenant` 章节；通用约束见 [`developer.md`](developer.md)。

## TenantContextHolder 用法

**核心类**：`TenantContextHolder`（位于 `structure-tenant`，包名疑似 `cn.structured.tenant.context.*`，使用前 grep 业务项目现有引用确认）。

| 方法 | 用途 |
|---|---|
| `TenantContextHolder.getTenantId()` | 获取当前租户 ID（业务侧主要使用） |
| `TenantContextHolder.setTenantId(tenantId)` | 设置当前租户 ID（框架 / 上游写入） |
| `TenantContextHolder.clear()` | 清理当前租户上下文（**MUST 在请求结束调用**） |

```java
@Service
public class OrderQueryService {

    public String getCurrentTenantId() {
        // ✅ 从上下文取
        return TenantContextHolder.getTenantId();
    }

    /** 跨租户批处理 / 内部 RPC 场景：手动设置租户上下文 */
    public void executeInTenant(String tenantId, Runnable task) {
        TenantContextHolder.setTenantId(tenantId);
        try {
            task.run();
        } finally {
            TenantContextHolder.clear();  // MUST finally 清理，防 ThreadLocal 泄漏
        }
    }
}
```

## 租户标识获取方式（铁律）

- **MUST** 业务代码通过 `TenantContextHolder.getTenantId()` 获取租户标识。
- **禁止** 从 `@RequestHeader` / `@RequestParam` / body 读取 `tenantId` 后直接使用。
- **禁止** 在业务 SQL 中显式追加 `WHERE tenant_id = ?` —— 由 MyBatis-Plus 租户插件自动注入。

> **原因**：租户标识由 `structure-gateway` 的 `TenantIdentificationFilter` 识别并写入上下文，业务侧直接读请求参数会绕过网关的租户校验与限流。

## MyBatis-Plus 租户插件配置

租户隔离由 `structure-tenant-starter` + `structure-datascope-mybatis-plus` 协作完成：

| 类 | 职责 |
|---|---|
| `StructureTenantLineHandler` | 租户行处理器，自动为 SQL 注入 `tenant_id` 条件 |
| `DataScopeInterceptor` | 数据权限拦截器（与租户插件协同） |
| `MyMetaObjectHandler` | 自动填充 `create_by` / `update_by` / `tenant_id` |

```yaml
structure:
  tenant:
    enabled: true
    default-tenant-id: "1"
    header:
      enabled: true
      name: "X-Tenant-Id"
    param:
      enabled: true
      name: "tenantId"
    resolver-order:
      - "header"
      - "param"
```

- **MUST** 引入 `structure-tenant-starter`；行级权限场景额外引入 `structure-datascope-mybatis-plus`。
- **SHOULD** 租户识别顺序通过 `resolver-order` 显式声明，避免依赖默认值。
- **MUST** 所有业务表含 `tenant_id` 字段，由插件自动管理，业务代码不显式赋值。

## 租户隔离级别

| 隔离级别 | 实现方式 | 适用场景 |
|---|---|---|
| 行级隔离（默认） | 共享表 + `tenant_id` 列，插件自动注入条件 | 中小型 SaaS、租户量多 |
| 数据库级隔离 | 每租户独立数据库 / Schema | 强隔离需求、合规要求 |

- **MUST** 默认采用 **行级隔离**（生态默认），由 `StructureTenantLineHandler` 自动注入 `tenant_id` 条件。
- **MAY** 数据库级隔离通过多数据源路由实现，但需额外配置，**SHOULD** 与架构师确认。

## 跨租户操作限制

- **MUST** 跨租户操作（批处理 / 内部 RPC / 运维脚本）通过 `executeInTenant(tenantId, task)` 显式切换上下文，**MUST 用 try-finally 清理**。
- **禁止** 业务接口允许前端指定任意 `tenantId` 跨租户读写 —— 跨租户权限 MUST 由后台授权。
- **MUST** 跨租户消息事件经 `DataScopeStreamBridge`（`EventManager` + `MESSAGE_EVENT` 自动路由），租户上下文随消息 Header 传递。

## 租户数据初始化

- **MUST** 新建租户时初始化基础数据（角色 / 权限 / 默认配置），通过领域事件 `TenantCreatedEvent` 触发（`EventManager.publish` + `MESSAGE_EVENT`）。
- **SHOULD** 租户初始化脚本走 Flyway 业务数据迁移，**禁止**硬编码租户 ID 在已提交的迁移文件中。

## 与数据权限 / 缓存的协作

| 场景 | 工具 | 租户隔离方式 |
|---|---|---|
| Redis 操作 | `DataScopeRedisTemplate` | key 自动按租户隔离 |
| 缓存操作 | `DataScopeCacheManager` | 缓存键自动按租户隔离 |
| 消息事件 | `DataScopeStreamBridge` | 租户上下文随 Header 传递 |

- **MUST** 缓存用 `DataScopeRedisTemplate` / `DataScopeCacheManager`，**禁止**原生 `RedisTemplate` / `CacheManager`（会跨租户共享缓存）。
- **禁止** 跨租户共享缓存 —— DataScope 强制隔离。

## 与其他规则的关系

- 数据权限设计：[`data-permission.md`](data-permission.md)
- 事件驱动与租户传递：[`event-driven.md`](event-driven.md)
- 网关租户识别（Filter 链）：[`components.md`](components.md) 第 5 节
