# 事件驱动设计 — structure-projects 事件约束

> 本文是事件发布与消费的 **速查手册**，覆盖 EventManager、StreamEvent、路由处理器与数据权限桥接。
> 组件级 API 详见 [`components.md`](components.md) 第 4 节；通用约束见 [`developer.md`](developer.md)。

## EventManager.publish 用法

生态唯一的事件发布入口，按 `EventChannel` 路由到不同底层实现：

| 事件声明 channel | 实际行为 | 数据权限包装 |
|---|---|---|
| `SPRING_EVENT` | `ApplicationEventPublisher.publishEvent(event)`，仅本 JVM | 不涉及 |
| `MESSAGE_EVENT` | `DataScopeStreamBridge.send(eventId, event)`，跨服务 | ✅ 自动包装 |
| `DEFAULT`（默认） | 读 `structure.infra.default-event-channel` 配置决定 | 视配置而定 |

```java
// application 层发布
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final EventManager eventManager;

    @Override
    public void createOrder(OrderDTO dto) {
        // ... 业务逻辑 ...
        eventManager.publish(OrderCreatedEvent.builder()
            .eventId("order-created")
            .orderId(order.getId())
            .build());
    }
}
```

- **MUST** 业务事件实现 `cn.structure.infra.event.Event` 接口（声明 `getEventId()`，按需重写 `getEventChannel()`）。
- **MUST** 通过注入 `EventManager` 调用 `publish(event)`。**禁止**直接 `@Autowired ApplicationEventPublisher` 或直连 `StreamBridge` / `DataScopeStreamBridge` / MQ client。
- **MUST** 跨服务事件显式 `getEventChannel() = EventChannel.MESSAGE_EVENT`（确保走 `DataScopeStreamBridge`，数据权限才能传递）。
- **SHOULD** 事件在 **application 层** 发布（事务提交后用 `@TransactionalEventListener` 或事务后置处理）。

## StreamEvent 定义

用于 Router 路由模型的 **信封**，承载 `eventType` / `businessType` / `payload` / `traceId` / `headers`：

```java
// 构造信封
StreamEvent<OrderPayload> envelope = StreamEvent.of("order", "create", payload);
// 或 Builder
StreamEvent.<OrderPayload>builder()
    .eventType("order")
    .businessType("create")
    .payload(payload)
    .build();
```

- **SHOULD** 仅在「一个 exchange 承载多种 eventType/businessType」时使用信封；简单场景用 Binding 模型直接发领域事件。

## @StreamRouteHandler 注解

按 `eventType` / `businessType` 多路复用的处理器注解：

```java
@Component
public class OrderRouteHandler {

    @StreamRouteHandler(eventType = "order", businessType = "create")
    public void onCreate(OrderPayload payload, StreamEvent<OrderPayload> event) {
        // payload 已按类型匹配；event 含 eventId/traceId/headers
    }

    @StreamRouteHandler(eventType = "order", businessType = "*",
                        condition = "#payload.amount > 100")
    public void onLargeOrder(OrderPayload payload, StreamEvent<OrderPayload> event) { ... }
}
```

4 步路由匹配（按顺序）：`eventType` 精确 → `businessType` 通配（`*`）→ `payloadType` 类型 → `condition` SpEL（用 `#payload` 引用）。

- **MUST** 处理器签名 `(T payload, StreamEvent<T> event)` 双参（不是单参）。
- **MUST** `eventType` 必填；`businessType` 可选，支持 `*` 通配。
- **SHOULD** 配一个统一入口 `Consumer<Message<StreamEvent<?>>>` 调用 `streamEventRouter.route(event)` 分发。

## DataScopeStreamBridge 数据权限桥接

| 类 | 用途 |
|---|---|
| `cn.structured.datascope.message.wrapper.DataScopeStreamBridge` | 包装原生 `StreamBridge`，发送前经 `DataScopeMessageUtils.injectDataScopeIntoMessage()` 注入数据权限 Header |

注入的 Header：`DATA_SCOPE_INFO`（完整 JSON）、`USER_ID`、`ORG_ID`、`DEPT_IDS`、`ROLES`、`PERMISSIONS`。

- **MUST** 跨服务消息事件经 `DataScopeStreamBridge`（而非原生 `StreamBridge`）。
- **MUST** 实际业务 **不直接注入 `DataScopeStreamBridge`**，而是通过 `EventManager.publish(event)` + `EventChannel.MESSAGE_EVENT` 自动路由。
- **禁止** 跳过包装类直接注入 `StreamBridge` —— 数据权限参数将无法跨服务传递（规则 12）。

## 事件发布时机（application 层）

| 场景 | 时机 | 注解 |
|---|---|---|
| 同 JVM 解耦 | 事务提交后 | `@TransactionalEventListener` |
| 跨服务通知 | 业务操作成功后 | `EventManager.publish` + `MESSAGE_EVENT` |
| 异步副作用 | 事务提交后异步 | `@Async` + `@TransactionalEventListener` |

- **MUST** 事件发布在 **application 层**（Service 编排内），**禁止**在 `domain` / `infra` 层发布跨服务事件。
- **SHOULD** 跨服务事件在事务提交后发布，避免事务回滚后误发事件。

## 事件处理（infrastructure 层）

消费侧三种模式按场景选：

| 模式 | 适用 | 关键约束 |
|---|---|---|
| Spring 事件（本 JVM） | 解耦同服务模块 | `@EventListener` / `@TransactionalEventListener` |
| Binding 监听（跨服务推荐） | 单一事件类型 | `Consumer` Bean 名 = `@StreamEventListener.bindingName` |
| Router 路由（复杂） | 多 eventType 复用 | `StreamEvent<T>` 信封 + `@StreamRouteHandler` |

- **MUST** Binding 模型中 `Consumer<Message<T>>` Bean 名与 `@StreamEventListener.bindingName` **完全一致**。
- **MUST** `Consumer` 内部只 `streamEventManager.dispatch(bindingName, payload)`，**禁止**直接写业务逻辑。
- **SHOULD** 多状态拆分用 `condition` SpEL（`#event.xxx`），不用 `if-else`。

## 事件幂等性

- **MUST** 消费端 **默认假设事件可能重复投递**，处理逻辑 MUST 幂等（基于业务唯一键去重 / 状态机校验）。
- **SHOULD** 事件携带 `eventId` / `traceId`，消费端用 Redis 记录已处理 `eventId` 防重。

## 事件版本兼容

- **MUST** 事件字段 **只增不删**，新增字段给默认值，**禁止**删除或重命名已有字段。
- **SHOULD** 重大变更通过新增 `eventType` 或 `businessType` 区分，而非修改旧事件结构。
- **SHOULD** 事件 POJO 用 `@Data @Builder @NoArgsConstructor @AllArgsConstructor`，**MUST 有无参构造**（规则 10）。

## 与其他规则的关系

- 数据权限包装完整 API：[`components.md`](components.md) 第 3 节 + 第 4 节
- 数据权限设计：[`data-permission.md`](data-permission.md)
- 多租户与事件协作：[`multi-tenant.md`](multi-tenant.md)
