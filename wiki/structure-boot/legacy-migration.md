# 老项目迁移指南 — 单体 4 模块 → DDD 7+1

> 本文是老项目（单体 4 模块 + Manager 模式）向 DDD 7+1 模块迁移的 **操作手册**，让 AI 在迁移中保持业务连续。
> 目标结构见 [`project-scaffolding.md`](project-scaffolding.md)；DDD 模式见 [`ddd-patterns.md`](ddd-patterns.md)。

## 4 模块结构分析（源）

单体应用典型 4 模块结构（`structure-multi-module-template`）：

| 模块 | 内容 | 持久化模式 |
|---|---|---|
| `structure-{X}-api` | controller/ + 启动类 | — |
| `structure-{X}-biz` | service/ + manager/ + mapper/ + entity/ + assembler/ + config/ | Manager 模式（`IManager extends IService`） |
| `structure-{X}-common` | dto/ + vo/ + query/ + enums/ + exception/ + constant/ | — |
| `structure-{X}-dependencies` | 父 POM | — |

**特征**：Entity 兼做领域对象（直接带 `@TableId` / `@TableLogic`）；Manager 直接继承 `ServiceImpl<{X}Mapper, {X}Entity>`；Service 直接注入 Manager。

## 7+1 目标模块映射表

| 源（4 模块） | 目标（7+1 模块） | 迁移动作 |
|---|---|---|
| `api/controller/` | `interfaces/controller/api/` | 拆分管理 API 与开放接口 |
| `biz/service/` | `application/` | Service → `I{X}Service` + `{X}ServiceImpl` |
| `biz/manager/` + `biz/mapper/` | `infra/` + `repository-mybatis/` | Manager → RepositoryFacade + Delegate |
| `biz/entity/` | `domain/` + `repository-mybatis/` | Entity 拆分为 `{X}Entity` + `{X}PO` |
| `biz/assembler/` | `application/` | 保留 `{X}Assembler` |
| `biz/config/` | `boot/` + 各模块 `config/` | 按职责分散 |
| `common/` | `common/` | 基本不变 |
| `api/` 启动类 | `boot/` | 独立启动模块 |
| `dependencies/` | `dependencies/` | 升级 parent 版本 |

## Manager → RepositoryFacade 迁移步骤

1. **新建接口**：在 `domain/repository/` 定义 `{X}Repository extends ICrudRepository<{X}Entity, ID>`，仅声明框架未覆盖的自定义方法。
2. **新建 Delegate 接口**：在 `infra/repository/delegate/` 定义 `{X}RepositoryDelegate`，声明自定义持久化方法。
3. **新建 RepositoryImpl**：在 `infra/repository/` 新建 `{X}RepositoryImpl extends RepositoryFacade<{X}Entity, ID, {X}RepositoryDelegate>`，方法体 `getDelegate().xxx()`。
4. **新建 MybatisPlusDelegate**：在 `repository-mybatis/repository/` 新建 `{X}MybatisPlusDelegate extends MybatisPlusRepositoryDelegate<{X}Entity, {X}PO, ID>`，把原 Manager 的查询逻辑搬入，**MUST 重写 `toEntity`/`toPo`**。
5. **删除 Manager**：原 `IManager extends IService` 与 `ManagerImpl` 整体替换为上述仓储体系。

```java
// 迁移前：Manager 模式
public interface OrderManager extends IService<OrderEntity> {
    List<OrderEntity> findByUserId(Long userId);
}

// 迁移后：RepositoryFacade + Delegate
// domain 层
public interface OrderRepository extends ICrudRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserId(Long userId);
}
// infra 层
public class OrderRepositoryImpl
        extends RepositoryFacade<OrderEntity, Long, OrderRepositoryDelegate>
        implements OrderRepository {
    @Override
    public List<OrderEntity> findByUserId(Long userId) {
        return getDelegate().findByUserId(userId);
    }
}
```

- **MUST** 迁移后 **禁止** 在 Service / Controller 注入 `Mapper` 或 `Manager`。
- **MUST** 原 Manager 的 `ServiceImpl` 基类方法（`save` / `getById` / `page`）由 `ICrudRepository` 提供，**禁止**重复定义。

## Service → application 迁移步骤

1. **拆分接口与实现**：`I{X}Service` 接口 + `{X}ServiceImpl` 实现移入 `application/`。
2. **替换依赖**：将 `Manager` 依赖替换为 `{X}Repository` 接口注入。
3. **事件迁移**：原直接调用 / 异步逻辑改为 `EventManager.publish(event)`（见下文）。
4. **装配迁移**：`{X}Assembler` 移入 `application/`，保持静态 `assembler()` 方法。
5. **异步任务**：原 `@Async` 方法移入 `application/async/`（`{X}Async`）。

- **MUST** Service 只依赖 `domain/repository/{X}Repository` 接口，**禁止**依赖 `infra` 实现类。

## PO / Entity 分离迁移

| 维度 | 迁移前（单体） | 迁移后（DDD） |
|---|---|---|
| 类 | `OrderEntity`（兼领域 + 持久化） | `OrderEntity`（domain）+ `OrderPO`（repository-mybatis） |
| 注解 | `@TableName` / `@TableId` / `@TableLogic` 混在 Entity | 持久化注解仅在 `OrderPO` |
| 转换 | 无（同一个对象） | `toEntity(PO)` / `toPo(Entity)` 双向转换 |

- **MUST** 剥离领域行为到 `OrderEntity`，持久化字段映射留在 `OrderPO`。
- **MUST** `{X}MybatisPlusDelegate` 显式实现 `toEntity` / `toPo`（规则 3，不重写有隐藏问题）。
- **MAY** 迁移期保留旧 Entity 作为 PO 的过渡形态，但**SHOULD**尽快完成分离。

## 事件迁移（→ EventManager.publish）

| 迁移前（单体） | 迁移后（DDD） |
|---|---|
| 直接调用其他 Service | `EventManager.publish(event)` 解耦 |
| `ApplicationEventPublisher` | `EventManager.publish(event)` + `Event` 接口 |
| 跨服务裸 `StreamBridge` | `EventManager.publish` + `MESSAGE_EVENT`（走 `DataScopeStreamBridge`） |

- **MUST** 业务事件实现 `cn.structure.infra.event.Event` 接口，通过 `EventManager.publish` 发布。
- **MUST** 跨服务事件声明 `EventChannel.MESSAGE_EVENT`，确保数据权限包装生效。
- 详细事件约束见 [`event-driven.md`](event-driven.md)。

## 兼容性保证策略（双写期 / 灰度切换）

### 双写期（推荐）

1. **新仓储与旧 Manager 并存**：新 `RepositoryFacade` 写入新表 / 新字段，旧 Manager 继续服务旧接口。
2. **数据同步**：通过领域事件 / Canal 同步双写数据，保持两侧一致。
3. **流量切换**：新接口走新仓储，旧接口逐步下线。

### 灰度切换

1. **按租户 / 按流量比例** 路由到新旧实现。
2. **监控对齐**：对比新旧接口的响应与数据一致性。
3. **回滚预案**：保留旧实现可快速切回。

- **MUST** 迁移期不破坏既有 API 契约（URL / 出入参结构不变）。
- **MUST** 数据库变更通过 **新增 Flyway 版本文件**，**禁止**修改已提交迁移文件。
- **SHOULD** 双写期设定明确截止时间，避免长期双轨。

## 迁移验证清单

### 结构验证
- [ ] 7+1 模块骨架完整，依赖方向自上而下（`domain ← application ← infra ← interfaces ← boot`）？
- [ ] 包名区分 `cn.structured.*`（业务）vs `cn.structure.*`（仅 common/infra）？
- [ ] `repository-mybatis` 包名是否与用户确认（沿用 `repository.repository.*` 或修正为 `repository.mybatis.*`）？

### 持久化验证
- [ ] 所有 `{X}RepositoryImpl` 继承 `RepositoryFacade` 且方法体仅 `getDelegate().xxx()`？
- [ ] 所有 `{X}MybatisPlusDelegate` 继承 `MybatisPlusRepositoryDelegate` 且 **重写** `toEntity`/`toPo`？
- [ ] 仓储接口优先用 `ICrudRepository` 已定义方法，未重复定义 `save`/`findById`？
- [ ] Service / Controller 不再注入 `Mapper` / `Manager`？

### 跨切面验证
- [ ] 非控制层用 `UserContext` 静态方法（而非 `SecurityUtils`）？
- [ ] 缓存用 `DataScopeCacheManager` / Redis 用 `DataScopeRedisTemplate`？
- [ ] 跨服务事件用 `EventManager.publish` + `MESSAGE_EVENT`？
- [ ] 租户标识从 `TenantContextHolder` 取，SQL 不手写 `WHERE tenant_id = ?`？

### 质量验证
- [ ] 迁移功能有对应单元测试，`mvn clean test` 全部通过？
- [ ] 业务流程集成测试（`XxxIT`）通过？
- [ ] 双写期新旧数据一致性校验通过？

## 与其他规则的关系

- 目标模块布局：[`project-scaffolding.md`](project-scaffolding.md)
- DDD 模式详解：[`ddd-patterns.md`](ddd-patterns.md)
- ORM 设计（MyBatis-Plus）：[`orm-design.md`](orm-design.md)
- 单体形态（不强制迁移）：[`project-scaffolding.md`](project-scaffolding.md) 第 3.3 节
