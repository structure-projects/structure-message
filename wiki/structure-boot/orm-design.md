# ORM 设计规范 (MyBatis-Plus) — structure-projects 持久化约束

> 本文是 DDD 项目中 MyBatis-Plus 用法的 **速查手册**，覆盖 BaseMapper 扩展、Wrapper、分页、逻辑删除与 Delegate 协作。
> 通用分层见 [`ddd-patterns.md`](ddd-patterns.md)；组件配置见 [`components.md`](components.md) 第 1 节。

## BaseMapper 扩展（IBaseMapper）

生态通过 `structure-mybatis-plus-starter` 提供 `IBaseMapper`，扩展原生 `BaseMapper`：

| 方法 | 用途 |
|---|---|
| `insertList(List<T>)` | 批量插入 |
| `selectJoinPageList(IPage, Wrapper)` | 关联分页查询 |
| `logicDeleteById(Serializable id)` | 按 ID 逻辑删除 |

```java
@Mapper
public interface OrderMapper extends IBaseMapper<OrderPO> {
    IPage<OrderVO> selectJoinPageList(IPage<OrderVO> page, @Param("ew") Wrapper<OrderVO> w);
}
```

- **MUST** Mapper 继承 `IBaseMapper<{X}PO>`（而非原生 `BaseMapper`），以获得批量/关联能力。
- **MUST** Mapper 仅存在于 `repository-mybatis` 模块，**禁止**暴露到 `application` / `interfaces` 层。

## Wrapper 用法（LambdaQueryWrapper）

复杂查询在 Delegate 内使用 `baseMapper` + `Wrappers.<{X}PO>lambdaQuery()`：

```java
// repository-mybatis 层：{X}MybatisPlusDelegate 内
public List<OrderEntity> findByUserId(Long userId) {
    List<OrderPO> list = baseMapper.selectList(
        Wrappers.<OrderPO>lambdaQuery()
            .eq(OrderPO::getUserId, userId)
            .eq(OrderPO::getIsDeleted, 0)
            .orderByDesc(OrderPO::getCreateTime)
    );
    return list.stream().map(this::toEntity).toList();
}
```

- **MUST** 优先用 `LambdaQueryWrapper`（类型安全、重构友好），**禁止**用字符串列名的 `QueryWrapper`（除动态 SQL 场景）。
- **SHOULD** 简单等值查询交给基类（非空字段自动组装 `QueryWrapper`），无需手写 Wrapper。

## 分页插件配置

MyBatis-Plus 分页插件由 `structure-mybatis-plus-starter` 自动装配。MP `Page` 需转换为生态 `ResPage`：

```java
import cn.structured.mybatis.plus.starter.convert.ResPageConvert;

Page<OrderPO> page = new Page<>(reqPage.getPage(), reqPage.getSize());
Page<OrderPO> result = orderMapper.selectPage(page, wrapper);
ResPage<OrderVO> resPage = ResPageConvert.convert(result, OrderAssembler::toVO);
```

- **MUST** 分页接口签名统一为 `page({X}Query query, ReqPage reqPage)`（规则 7）。
- **MUST** 分页响应用 `cn.structure.common.vo.ResPage<T>`，请求用 `cn.structure.common.vo.ReqPage`。
- **MUST** 用 `ResPageConvert.convert(...)` 转换，**禁止**直接返回 MP `Page`。

## @TableLogic 逻辑删除

```java
public class OrderPO {
    @TableLogic
    private Integer isDeleted;  // 0 未删除 / 1 已删除
}
```

- **MUST** 所有业务表含 `is_deleted` 字段并标注 `@TableLogic`。
- **MUST** 逻辑删除字段值约定：`0` 未删除、`1` 已删除。
- **SHOULD** 配合 `MyMetaObjectHandler` 自动填充审计字段（`create_by` / `update_by` / `create_time` / `update_time`）。

## @TableField 自动填充

```java
public class OrderPO {
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
}
```

- **MUST** 审计字段用 `@TableField(fill = ...)` 声明，由 `MyMetaObjectHandler`（`structure-datascope-mybatis-plus`）自动填充。
- **MUST** `createBy` / `updateBy` 从 `UserContext.getLongUserId()` 取值，**禁止**手动传入。

## 批量操作

| 方式 | 场景 | 约束 |
|---|---|---|
| `ICrudRepository.saveBatch(List<T>)` | 业务批量保存 | 走 RepositoryFacade，自动判空返回 |
| `IBaseMapper.insertList(List<T>)` | Mapper 层批量插入 | 仅 Delegate 内使用 |
| `removeBatchByIds(List<ID>)` | 批量删除 | 走仓储接口 |

- **SHOULD** 批量操作优先走仓储接口（`saveBatch` / `removeBatchByIds`），而非直接调 Mapper。

## RepositoryImpl 中的 MyBatis-Plus 用法

```java
// infra 层
@Component("orderRepository")
public class OrderRepositoryImpl
        extends RepositoryFacade<OrderEntity, Long, OrderRepositoryDelegate>
        implements OrderRepository {

    // 框架已定义方法（save/findById/queryPage 等）无需重写
    // 仅自定义方法透传给 Delegate
    @Override
    public List<OrderEntity> findByStatus(OrderStatus status) {
        return getDelegate().findByStatus(status);
    }
}
```

- **MUST** `RepositoryImpl` 不直接持有 `Mapper`，所有持久化经 Delegate。
- **MUST NOT** 在 `RepositoryImpl` 重复定义 `save` / `findById` / `queryPage` 等框架已有方法。
- **MUST** `save` 由基类按 ID 是否为空自动区分 insert/update，**禁止**手写分支判断。

## ReadDelegate / WriteDelegate 与 Mapper 的关系

| 层 | 类 | 与 Mapper 的关系 |
|---|---|---|
| `infra` | `{X}RepositoryImpl` | 不接触 Mapper，透传 Delegate |
| `repository-mybatis` | `{X}MybatisPlusDelegate`（@WriteDelegate） | 持有 `baseMapper`，执行真实 CRUD |
| `repository-mybatis` | `{X}ReadDelegate`（@ReadDelegate） | 可指向只读库 / ES，失败回退写代理 |

- **MUST** Mapper 只在 Delegate 内被调用，是持久化的最底层。
- **SHOULD** 读多写少场景用 `CqrsRepositoryFacade` 分离读写 Delegate（读失败自动回退写代理）。
- 详细 CQRS 机制见 [`components.md`](components.md) 第 4 节「CQRS 读写分离」。

## 与其他规则的关系

- DDD 分层与仓储模式：[`ddd-patterns.md`](ddd-patterns.md)
- Flyway 迁移不可变规则：[`developer.md`](developer.md) 「Flyway 迁移文件不可变规则」
- 组件配置（Starter 清单）：[`components.md`](components.md) 第 1 节
