# 数据权限设计 — structure-projects 数据权限约束

> 本文是数据权限（行级 / 列级）的 **速查手册**，覆盖缓存包装、Redis 包装、规则引擎与配置。
> 组件级 API 详见 [`components.md`](components.md) 第 3 节；通用约束见 [`developer.md`](developer.md)。

## 数据权限包装工具（规则 12）

| 类 | 包 | 用途 |
|---|---|---|
| `DataScopeCacheManager` | `cn.structured.datascope.cache.manager` | 缓存管理器包装，按数据权限规则过滤/路由缓存访问 |
| `DataScopeRedisTemplate` | `cn.structured.datascope.redis.template` | RedisTemplate 包装，Redis 操作自动携带数据权限参数 |
| `DataScopeStreamBridge` | `cn.structured.datascope.message.wrapper` | StreamBridge 包装，消息发送前注入数据权限 Header |
| `CacheDataRuleEngine` | `cn.structured.datascope.cache.engine` | 缓存侧数据规则引擎 |
| `RedisDataRuleEngine` | `cn.structured.datascope.redis.engine` | Redis 侧数据规则引擎 |
| `MySqlDataRuleEngine` | `cn.structured.datascope.mybatis.engine` | MySQL 侧数据规则引擎 |

- **MUST** 缓存操作用 `DataScopeCacheManager`（替代 Spring `CacheManager`）。
- **MUST** Redis 操作用 `DataScopeRedisTemplate`（替代 `RedisTemplate` / `StringRedisTemplate`）。
- **MUST** 跨服务消息事件经 `DataScopeStreamBridge`（经 `EventManager` + `MESSAGE_EVENT` 自动路由）。
- **禁止** 跳过上述包装类直接注入原生 `CacheManager` / `RedisTemplate` / `StreamBridge` —— 数据权限参数将无法跨层/跨服务传递。

## DataScopeCacheManager 缓存策略

`DataScopeCacheManager` 包装 Spring `CacheManager`，按当前用户的数据权限规则过滤缓存访问：

- **MUST** 所有缓存 key 自动包含租户与用户维度，**禁止**跨租户/跨用户共享缓存。
- **SHOULD** 高频读数据加缓存，但缓存值 MUST 经规则引擎过滤后再返回。
- **MUST** 所有缓存 key MUST 设 TTL，**禁止**无过期缓存。

## DataScopeRedisTemplate 用法

```java
@Service
@RequiredArgsConstructor
public class OrderCacheService {

    private final DataScopeRedisTemplate<String, Object> redisTemplate;

    public void cacheOrder(OrderVO vo) {
        // key 自动按租户/用户隔离，无需手动拼接
        redisTemplate.opsForValue().set("order:" + vo.getId(), vo, 30, TimeUnit.MINUTES);
    }
}
```

- **MUST** 注入 `DataScopeRedisTemplate` 而非 `RedisTemplate`，Redis 操作自动携带数据权限参数。
- **MUST** 分布式锁用 `@RedisLock`（`structure-redis-starter`），key 经数据权限包装。

## DataRuleEngine.filter 规则引擎

编程式 **列级权限过滤**，按 `@DataScopeField` 标注的字段隐藏敏感数据：

```java
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final DataRuleEngine dataRuleEngine;

    @Override
    public OrderVO findById(Long id) {
        OrderEntity order = orderRepository.findById(id);
        OrderVO vo = OrderAssembler.toVO(order);
        dataRuleEngine.filter(vo, "order");  // 按 @DataScopeField 规则过滤字段
        return vo;
    }
}
```

- **MUST** DTO/VO 出参前调用 `dataRuleEngine.filter(dto, resourceName)`，确保 `@DataScopeField` 标注字段按当前用户角色/权限隐藏。
- **MUST** `resourceName` 与 DTO 类上 `@DataScopeRule(resource = "...")` 的值一致。

## 数据权限注解用法

| 注解 | 位置 | 作用 |
|---|---|---|
| `@DataScopeField` | DTO/VO 字段 | 标注需按权限过滤的列级字段 |
| `@DataScopeRule(resource = "...")` | DTO/VO 类 | 声明资源名，与 `filter(dto, resourceName)` 对应 |
| `@DataScopeRow` | 实体/方法 | 行级权限规则（详细机制待读源码） |

```java
@DataScopeRule(resource = "order")
public class OrderVO {
    private Long id;
    private String orderNo;

    @DataScopeField  // 无权限用户该字段被隐藏
    private BigDecimal amount;
}
```

## 权限规则配置

```yaml
structure:
  data-scope:
    enabled: true
    header-name: X-DataScope-Id
    role-header-name: X-DataScope-Roles
    permission-header-name: X-DataScope-Permissions
```

- **MUST** 引入 `structure-datascope-starter` + 对应存储模块（如 `structure-datascope-mybatis-plus`）。
- **SHOULD** 上游（gateway / Feign 调用方）按上述 Header 名传递数据权限参数，下游经 `DataScopeContext` 自动还原。

## 数据范围类型

| 范围 | 含义 | 典型场景 |
|---|---|---|
| 全部 | 可见所有数据 | 超级管理员 |
| 部门 | 仅可见本部门及下级部门 | 部门主管 |
| 部门及下级 | 可见本部门树下所有数据 | 区域负责人 |
| 个人 | 仅可见自己创建的数据 | 普通员工 |

- **SHOULD** 数据范围由角色绑定，运行时由 `DataScopeContext`（ThreadLocal）传递当前用户的范围。
- **MUST** `DataScopeContext.remove()` 在请求/任务结束调用，避免 ThreadLocal 内存泄漏。

## DataScopeContext API（ThreadLocal）

`cn.structured.datascope.DataScopeContext` 提供读 / 写 / 校验方法：

| 类别 | 方法 |
|---|---|
| 读 | `getUserId()` / `getOrgId()` / `getDeptIds()` / `getRoles()` / `getPermissions()` |
| 校验 | `hasRole(role)` / `hasPermission(perm)` / `hasAnyRole(...)` / `hasAnyPermission(...)` |
| 清理 | `remove()`（**MUST 请求结束调用**） |

## 缓存失效策略

- **MUST** 数据权限规则变更时，主动失效受影响缓存（按 `resourceName` 维度清理）。
- **SHOULD** 缓存 key 包含权限版本号，权限变更时递增版本号使旧缓存自然失效。
- **MUST** 分布式场景通过 Redis Pub/Sub 广播失效消息，确保多节点缓存一致。

## 与其他规则的关系

- 消息 Header 注入机制：[`components.md`](components.md) 第 3 节「消息 Header」
- 多租户与数据权限协作：[`multi-tenant.md`](multi-tenant.md)
- 事件驱动包装：[`event-driven.md`](event-driven.md)
