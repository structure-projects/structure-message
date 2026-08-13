# DDD 模式详解 — structure-projects 领域驱动设计约束

> 本文是 DDD 7+1 项目形态的 **模式速查手册**，让 AI 在 structure-boot 栈内正确落地分层、聚合、仓储与事件。
> 通用分层原则见 `wiki/_common/architecture.md`；模块布局见 [`project-scaffolding.md`](project-scaffolding.md)。

## 7+1 模块职责表（已验证）

每个业务服务是 monorepo，7 个后端模块 + 1 个父 POM 聚合模块：

| 模块 | 职责 | 关键产物 |
|---|---|---|
| `structure-{X}-dependencies` | 父 POM，版本管理（**仓库根无 pom.xml**） | `${revision}` CI-friendly 版本 |
| `structure-{X}-common` | 跨层契约：DTO / VO / Query / enums / exception / constant | `{X}DTO` / `{X}VO` / `{X}Query` / `{X}ExceptionEnum` |
| `structure-{X}-domain` | 领域核心：实体 / 仓储接口 / 领域服务 | `{X}Entity` / `{X}Repository` 接口 / `DomainService` |
| `structure-{X}-infra` | 仓储实现 / Delegate 接口 | `{X}RepositoryImpl` / `{X}RepositoryDelegate` |
| `structure-{X}-repository-mybatis` | 持久化细节：PO / Mapper / MybatisPlusDelegate / Flyway | `{X}PO` / `{X}Mapper` / `{X}MybatisPlusDelegate` |
| `structure-{X}-application` | 应用服务 / 编排 / 装配 / 异步 | `I{X}Service` / `{X}ServiceImpl` / `{X}Assembler` |
| `structure-{X}-interfaces` | 入口适配：管理 API + 开放接口 | `{X}Controller` / `Open{X}Controller` |
| `structure-{X}-boot` | 启动类 + 配置 + Dockerfile | `Application` / `application.yaml` |

## 模块依赖方向（铁律）

```
common ← domain ← infra ← repository-mybatis
                 ↑
        application → domain + infra
                 ↑
            interfaces → application
                 ↑
                 boot → all
```

- **MUST** 依赖只能自上而下，**禁止**反向 / 跨层跳跃依赖。
- **禁止** 在 `application` / `domain` / `interfaces` 层注入 `Mapper` 或 `PO`。
- **MUST** 业务代码只依赖 `domain/repository/{X}Repository` 接口，不依赖实现。

## RepositoryFacade 模式

业务仓储通过 `RepositoryFacade` 透传给 Delegate，屏蔽持久化细节。

```java
// infra 层：仅透传，不含业务逻辑
@Component("orderRepository")
public class OrderRepositoryImpl
        extends RepositoryFacade<OrderEntity, Long, OrderRepositoryDelegate>
        implements OrderRepository {

    @Override
    public List<OrderEntity> findByUserId(Long userId) {
        return getDelegate().findByUserId(userId);  // 透传给 Delegate
    }
}
```

- **MUST** `{X}RepositoryImpl` 继承 `cn.structure.infra.repository.RepositoryFacade<{X}Entity, ID, {X}RepositoryDelegate>`。
- **MUST** 方法体内只 `return getDelegate().xxx(...)`，**禁止**写持久化逻辑。
- **MUST** 仓储接口继承 `cn.structure.common.repository.ICrudRepository<T, ID>`（只读场景用 `IQueryRepository`），优先用框架已定义方法，**禁止**重复定义 `save`/`findById` 等。

## Delegate 模式（@ReadDelegate / @WriteDelegate）

Delegate 承接真实持久化操作，支持 CQRS 读写分离。

| 注解 | 位置 | 作用 |
|---|---|---|
| `@WriteDelegate` | Delegate 类/字段 | 标注写代理（insert/update/delete） |
| `@ReadDelegate` | Delegate 类/字段 | 标注读代理（query/select），失败自动回退到写代理 |

```java
// repository-mybatis 层
public class OrderMybatisPlusDelegate
        extends MybatisPlusRepositoryDelegate<OrderEntity, OrderPO, Long>
        implements OrderRepositoryDelegate {

    @Override  // MUST 显式重写
    public OrderEntity toEntity(OrderPO po) { return OrderAssembler.toEntity(po); }

    @Override  // MUST 显式重写
    public OrderPO toPo(OrderEntity entity) { return OrderAssembler.toPo(entity); }
}
```

- **MUST** `{X}MybatisPlusDelegate` 继承 `MybatisPlusRepositoryDelegate<E, P, ID>` 并 **显式重写** `toEntity(PO)` / `toPo(Entity)`（不重写会产生字段丢失、ID 未回填等隐藏问题）。
- **SHOULD** 读写分离用 `CqrsRepositoryFacade`（读优先走 `readDelegate`，异常回退 `baseDelegate`）。
- ⚠️ `@ReadDelegate` / `@WriteDelegate` 来自 `cn.structure.infra.annotations`（**无 d**，底层库）。

## Entity vs PO 分离

| 维度 | `{X}Entity`（领域实体） | `{X}PO`（持久化对象） |
|---|---|---|
| 所在模块 | `domain` | `repository-mybatis` |
| 注解 | `@Builder` + `@Getter` + `@NoArgsConstructor` | `@TableName` / `@TableId` / `@TableField` |
| 用途 | 表达领域概念、参与业务规则 | 映射数据库表 |
| 转换 | 由 `{X}MybatisPlusDelegate.toEntity/toPo` 双向转换 | 同左 |

- **MUST** Entity 不带 MyBatis-Plus 注解；PO 不含领域行为。
- **禁止** 在单体项目中套用此分离（单体形态 Entity 兼做领域对象，见 [`project-scaffolding.md`](project-scaffolding.md) 第 3.3 节）。

## 聚合根与值对象

- **MUST** 每个聚合以 **聚合根 Entity** 为唯一入口（如 `OrderEntity` 是订单聚合根，`OrderItemEntity` 通过聚合根访问）。
- **MUST** 聚合内的一致性由聚合根方法保证，**禁止**外部直接修改聚合内对象状态。
- **SHOULD** 无唯一标识的描述性对象建模为 **值对象**（如 `Address` / `Money`），用 `@Value` 或不可变类实现。
- **SHOULD** 跨聚合操作通过领域事件解耦，而非直接调用对方仓储。

## 限界上下文

- **MUST** 每个业务服务（`structure-{X}`）对应一个限界上下文，上下文边界 = 仓库边界。
- **MUST** 跨上下文集成通过 **API（Feign）** 或 **领域事件（EventManager）**，**禁止**跨上下文直接共享数据库表或仓储。
- **SHOULD** 上下文内的通用语言在 `{X}ExceptionEnum` / `{X}DTO` 命名中保持一致。

## 领域事件（EventManager.publish + StreamEvent）

- **MUST** 事件在 **application 层** 发布（事务提交后），通过 `cn.structure.infra.event.EventManager.publish(event)`。
- **MUST** 业务事件实现 `cn.structure.infra.event.Event` 接口；跨服务事件声明 `EventChannel.MESSAGE_EVENT`。
- **SHOULD** 复杂多路复用场景用 `StreamEvent<T>` 信封 + `@StreamRouteHandler`。
- 详细 API 见 [`event-driven.md`](event-driven.md) 与 [`components.md`](components.md) 第 4 节。

## 与其他规则的关系

- 模块布局与选型：[`project-scaffolding.md`](project-scaffolding.md)
- 开发约束（命名/异常/注入）：[`developer.md`](developer.md)
- ORM 细节（Wrapper/分页/逻辑删除）：[`orm-design.md`](orm-design.md)
- 单体形态（不套用 DDD）：[`project-scaffolding.md`](project-scaffolding.md) 第 3.3 节
