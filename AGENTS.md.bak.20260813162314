# AGENTS.md — 全栈项目 AI 规则

> 由 structure-agent-rules install.sh 自动生成。
> 安装的技术栈: structure-boot

## structure-boot


> 本文件是 **Codex / 通用 AI Agent** 在 structure-projects 业务项目中的工作规则。
> 由 [structure-agent-rules](https://github.com/structure-projects/structure-agent-rules) 仓库的 `codex/AGENTS.md` 模板复制而来。
>
> **使用方式**：将本文件放在业务项目根目录，Codex 启动时自动加载。
> **详细规则**（如能访问 structure-agent-rules 仓库）：`prompts/structure-boot/developer.md` / `prompts/structure-boot/architect.md` / `prompts/structure-boot/components.md` / `prompts/structure-boot/tester.md` / `prompts/structure-boot/reviewer.md` / `prompts/structure-boot/validation.md` / `prompts/structure-boot/swagger.md` / `prompts/structure-boot/ci-cd.md`。

---

## 1. 生态硬约束（任何任务都必须遵守）

- Maven `groupId` = `cn.structured`；npm scope = `@structure-projects`。
- 包名：`cn.structure.*`（**无 d**）**仅**用于 `structure-common` / `structure-infra` 等底层基础库；其余全部 `cn.structured.*`（**有 d**）。
  ⚠️ `structure-security` 是 `cn.structured.security`（有 d），**不是** `cn.structure.security`。
  ⚠️ `structure-security` 内部 `jwt-starter` 包名是 `cn.structure.starter.jwt.*`（无 d），其他 starter 是 `cn.structured.starter.*`（有 d）。
- 当前主线：Spring Boot `4.0.6` + JDK 17 + `jakarta.*`（**不要写 `javax.*`**）；MyBatis-Plus `3.5.16`；Spring Cloud `2025.1.0`。
- 项目 parent：`cn.structured:structure-dependencies:1.4.4`；版本号用 `${revision}`。
- **禁止** 引入 `structure-cloud-dependencies`（structure-cloud 已停止维护）。
- **禁止** 在新项目中引入 `structure-ruoyi` / `ruoyi-framework` / `structure-yudao`（多数 2024-09 停更）。

## 2. 模块布局（DDD 7+1 + 前端 monorepo）

```
structure-{X}/
├── structure-{X}-dependencies/        # 父 POM（无根 pom.xml），<modules>+相对路径聚合
├── structure-{X}-common/              # DTO / VO / Query / enums / exception / constant
├── structure-{X}-domain/              # {X}Entity / {X}Repository 接口 / DomainService
├── structure-{X}-infra/               # {X}RepositoryImpl / {X}RepositoryDelegate 接口
├── structure-{X}-repository-mybatis/  # {X}PO / {X}Mapper / {X}MybatisPlusDelegate / Flyway
├── structure-{X}-application/         # I{X}Service / {X}ServiceImpl / {X}Assembler / {X}Async
├── structure-{X}-interfaces/          # controller/api/{X}Controller + controller/open/Open{X}Controller
├── structure-{X}-boot/                # 启动类 + application.yaml
├── structure-{X}-ui/                  # wujie 微前端子应用
└── structure-{X}-ui-components/       # 本地组件库（file: 引用，正式发版时发 npm）
```

依赖方向：`common → domain → infra → repository-mybatis`；`application → domain+infra`；`interfaces → application`；`boot → all`。**禁止** 反向 / 跨层依赖。

## 3. 关键优先级（顺序不可乱）

- **工具类**：Hutool → 框架 `structure-common` → 框架其他模块 → 自定义（**限 infra 层**）。
- **Bean 注入**：构造器（推荐 Lombok `@RequiredArgsConstructor`）→ `@Resource` → `@Autowired`（谨慎）。

## 4. 持久化（DDD 项目）

- **MUST** 业务代码只依赖 `domain/repository/{X}Repository` 接口，且接口继承 **`cn.structure.common.repository.ICrudRepository<T, ID>`**。
- **MUST** 优先使用框架已定义函数（**禁止重复定义**）：
  - 读：`queryById` / `queryByIdOptional` / `queryOne` / `queryOneOptional` / `queryList` / `queryPage(ReqPage)`
  - 写：`save` / `saveBatch` / `removeById` / `removeBatchByIds`
  - 其他：`findById` / `listByIds` / `count` / `exists`
- **MUST** `infra/repository/{X}RepositoryImpl` 继承 `cn.structure.infra.repository.RepositoryFacade<{X}Entity, Long, {X}RepositoryDelegate>`，方法体内 `return getDelegate().xxx(...)`。
- **MUST** `repository-mybatis/repository/{X}MybatisPlusDelegate` 继承 `cn.structure.infra.mybatis.plus.repository.MybatisPlusRepositoryDelegate<{X}Entity, {X}PO, Long>`，**显式重写 `toEntity(PO)` 与 `toPo(Entity)`**（不重写会有隐藏问题：字段丢失、默认值错乱、ID 未回填）。
- **MUST** 复杂查询在 Delegate 内使用 `baseMapper` + `Wrappers.<{X}PO>lambdaQuery()`。
- **禁止** 在 `Service` / `Controller` 中直接注入 `Mapper` 或 `PO`。
- **SHOULD** 读写分离用 `@WriteDelegate` / `@ReadDelegate`（`cn.structure.infra.annotations`）；CQRS 场景继承 `CqrsRepositoryFacade<T, ID, D, RD>`，读操作优先 `readDelegate` 失败自动回退。
- ⚠️ **包路径异常**：`repository-mybatis` 模块包是 `cn.structured.{X}.repository.repository.*`（**双 "repository"**）。新代码前 MUST 与用户确认沿用还是修正为 `.repository.mybatis.*`。

## 5. POJO 规范

- **MUST** 领域实体提供 `@Builder`（优先 Lombok）。
- **MUST** 所有 POJO（Entity / PO / DTO / VO / Query）**必须有无参构造方法** —— MyBatis、Jackson 反射依赖。
- **MUST** 函数参数 ≤ 3，超过用 包装类 / 值对象 / 命令对象（`{X}Command`）。

## 6. 异常与响应

- **MUST** 业务异常有对应 `{X}ExceptionEnum` 枚举（错误码形如 `ORG_001`），**禁止**字符串字面量。
- **MUST** 业务层（application / domain / infra）抛 `cn.structure.common.exception.CommonException`（直接或间接子类）。
- **MUST** 控制层（interfaces）**不抛异常**，用 `cn.structure.common.utils.ResultUtilSimpleImpl.fail(code, message)` 返回失败；成功用 `success(data)`。
- **MUST** Controller 返回 `cn.structure.common.entity.ResResultVO<T>`；分页响应 `cn.structure.common.vo.ResPage<T>`，请求 `cn.structure.common.vo.ReqPage`。
- **禁止** `throw new RuntimeException(...)` 或返回裸 `Map` / `String`。

## 7. API 出入参与命名

- **MUST** API 出入参用 DTO / VO / Query 三族，兼容 CQRS：写入参 `{X}DTO` / `{X}Command`，读入参 `{X}Query`，出参 `{X}VO`。
- **MUST** 分页签名统一：
  ```java
  ResPage<XxxVO> page(XxxQuery query, ReqPage reqPage);
  ```
- **MUST** 函数命名见名知意且相同功能命名统一：CRUD 标准为 `create` / `update` / `delete` / `findById` / `page`。**不要** `list`/`page`/`queryPage` 混用。

## 8. 命名约定

| 类型 | 模式 |
|---|---|
| 领域实体 | `{X}Entity`（**非** `{X}`） |
| 持久化对象 | `{X}PO` |
| 仓储接口 / 实现 | `{X}Repository` / `{X}RepositoryImpl` |
| Delegate 接口 / MyBatis 实现 | `{X}RepositoryDelegate` / `{X}MybatisPlusDelegate` |
| Service 接口 / 实现 | `I{X}Service` / `{X}ServiceImpl` |
| Controller | `{X}Controller`（管理 API `/api/{资源}`） / `Open{X}Controller`（开放接口） |
| Assembler | `{X}Assembler`（私有构造 + 静态 `assembler()` 方法） |
| 错误码枚举 | `{X}ExceptionEnum`（码如 `ORG_001`） |
| 自定义工具类 | `{X}Util` / `{X}Utils`，**放 infra 层** |

## 9. 用户上下文（规则 13）

- **控制层**：`cn.structured.security.util.SecurityUtils` 或 **`cn.structured.security.context.UserContext`** 均可。
- **非控制层（Service / Domain / Infra / Assembler / 异步任务）**：**MUST 用 `UserContext` 静态方法**，**无需注入**。

**`UserContext` 常用静态方法**（`cn.structured.security.context.UserContext`，位于 `structure-security-core`）：

| 方法 | 返回 |
|---|---|
| `UserContext.get()` | `UserContextEntity`（可空） |
| `UserContext.getLongUserId()` | `Long`（**推荐**，免手写 `Long.parseLong`） |
| `UserContext.getUserId()` | `String` |
| `UserContext.getLongDeptId()` / `getLoneDeptIds()` | `Long` / `Set<Long>` |
| `UserContext.getLongRoles()` / `getLongPermissions()` | `Set<Long>` |

```java
// ✅ 推荐
Long userId = UserContext.getLongUserId();
if (userId == null) { throw new OrderException(OrderExceptionEnum.NOT_LOGGED_IN); }

// ❌ 避免（框架已提供 getLongUserId）
UserContextEntity e = UserContext.get();
if (e != null) { return Long.parseLong(e.getUserId()); }
```

⚠️ **包名陷阱**：`UserContext` 在 `cn.structured.security.context.*`（`structure-security-core`），**不是** `cn.structured.starter.context.*`（那是底层 SPI `IContextManager` 所在）。
⚠️ **已知拼写 bug**：`UserContext.getLoneDeptIds()` 应为 `getLongDeptIds()`，业务使用是合理的。

## 10. 数据权限（规则 12）

- **MUST** 跨服务消息事件 MUST 经 **`cn.structured.datascope.message.wrapper.DataScopeStreamBridge`**（替代原生 `StreamBridge`），业务实际通过 `EventManager.publish(event)` + `EventChannel.MESSAGE_EVENT` 自动路由。
- **MUST** 缓存操作使用 **`cn.structured.datascope.cache.manager.DataScopeCacheManager`**（替代 Spring `CacheManager`）。
- **MUST** Redis 操作使用 **`cn.structured.datascope.redis.template.DataScopeRedisTemplate`**（替代 `RedisTemplate` / `StringRedisTemplate`）。
- **禁止** 跳过上述包装类直接注入 `StreamBridge` / `CacheManager` / `RedisTemplate` —— 数据权限参数无法跨层/跨服务传递。

## 11. 事件（规则 11）

**发布**：
- **MUST** 业务事件实现 `cn.structure.infra.event.Event` 接口，声明 `getEventId()`，按需重写 `getEventChannel()`。
- **MUST** 通过注入 `cn.structure.infra.event.EventManager` 调用 `publish(event)`。**禁止**直接 `@Autowired ApplicationEventPublisher` 或直连 `StreamBridge` / MQ client。
- **MUST** 跨服务事件 MUST 显式 `getEventChannel() = EventChannel.MESSAGE_EVENT`。

**消费**（三种模式按场景选）：

| 模式 | 适用 | 关键约束 |
|---|---|---|
| Spring 事件 | 本 JVM | 标准 `@EventListener` / `@TransactionalEventListener` |
| **Binding 监听模型**（推荐） | 跨服务 | `Consumer<Message<T>>` Bean 名 = `@StreamEventListener.bindingName`；`Consumer` 内只 `streamEventManager.dispatch(...)`；多状态用 `condition` SpEL |
| Router 路由模型 | 一个 exchange 多种 eventType | 信封 `StreamEvent<T>`；处理器 `@StreamRouteHandler(eventType, businessType, condition)`，签名 `(T payload, StreamEvent<T> event)` 双参 |

## 12. 多租户

- **MUST** 租户标识从上下文取（由 `structure-gateway` / `structure-tenant` 写入）。
- **禁止** 从请求参数 / Header 读租户 ID 后直接使用。
- **禁止** 业务 SQL 手写 `WHERE tenant_id = ?` —— 由 `structure-datascope` 或框架自动处理。

## 13. 远程调用与 JSON

### Feign（MUST）

- **MUST** 服务间远程调用使用 Spring Cloud OpenFeign（`@FeignClient` + `@EnableFeignClients`）；**禁止** `RestTemplate` / `WebClient` / 手写 HTTP。
- **MUST** 优先使用 **Spring Cloud Alibaba**：Nacos（注册发现/配置）、Sentinel（熔断限流）、Seata（分布式事务）。
- **MUST** 每个 `@FeignClient` 声明 `fallback` / `fallbackFactory` —— 保证业务连续 + 单测可验证降级路径。
- **MUST** 强一致性场景（资金/库存/账务/状态机）：fallback 中 **抛 `CommonException` 中断业务**，**禁止** 静默返回兜底数据；跨服务强一致性 **SHOULD** 用 Seata。

### JSON（MUST）

- **MUST** JSON 序列化与工具方法优先 **FastJSON**（`JSON.toJSONString()` / `JSON.parseObject()`）。
- `structure-restful-web-starter` 已内置 FastJson 转换器（Long→String 防 JS 精度丢失）。
- **禁止** 业务代码混用 Jackson `ObjectMapper` / Gson。

## 14. 测试

### 测试工作流（MUST —— 与开发同步进行）

- **MUST** 每开发一个功能，**立即**编写对应单元测试；**单测通过后才能开始下一个功能**。
- **MUST** 功能代码有修改时，**同步修改对应测试代码**并保证通过。
- **MUST** 业务模块编写完成后，编写 **业务流程集成测试**（`XxxIT`），通过后业务才算交付。
- **MUST** 提交代码前：本地 `mvn clean test` 全部通过 + `mvn clean package -DskipTests` 编译通过。
- **禁止** 在测试失败或编译失败的情况下提交/合入/发布代码。

### 测试分层与有效性

- `XxxTest` — 单元测试，**不启动** Spring 上下文；`XxxIT` — 集成测试，**必须** 用真实中间件（Testcontainers）。
- **禁止** Mock 数据库 / Redis / MQ；**禁止** Mock 自己项目的 `Repository` / `Service`。
- **MUST** 覆盖：正常路径 + 异常路径 + 边界条件。
- **MUST** 断言有效（验证行为与数据）；**禁止** 僵尸断言（只 `assertNotNull` / 只看返回码 200）。
- **MUST** 覆盖：多租户隔离、统一异常返回统一错误码、`@ReadDelegate` 失败回退、`UserContext.getLongUserId()` 返回 null 的兜底。
- **禁止** `Thread.sleep` 等待异步（用 Awaitility）；**禁止** 无 issue 关联的 `@Disabled`。

## 15. 提交前自检

- [ ] 包名是否区分 `cn.structure.*`（仅 common/infra）vs `cn.structured.*`（其余全部）？
- [ ] 是否继承了 `RepositoryFacade` / `MybatisPlusRepositoryDelegate` 并 **重写了** `toEntity`/`toPo`？
- [ ] 仓储接口是否继承 `ICrudRepository`，未重复定义框架已有方法？
- [ ] Controller 是否返回 `ResResultVO<T>` 并经 `ResultUtilSimpleImpl` 构造？
- [ ] 业务异常是否用 `{X}ExceptionEnum` 抛 `CommonException`？
- [ ] Service 接口是否带 `I` 前缀？Entity 是否带 `Entity` 后缀？
- [ ] Bean 注入是否优先构造器？
- [ ] 非控制层是否用 `UserContext.getLongUserId()` 等静态方法？
- [ ] 缓存 / Redis / 消息事件是否走了框架的数据权限包装工具？
- [ ] 分页签名是否为 `page({X}Query query, ReqPage reqPage)`？
- [ ] 租户上下文是否来自框架而非请求参数？
- [ ] **本次开发的功能是否都有对应单元测试并通过？**
- [ ] **修改的既有功能，其测试是否已同步更新并通过？**
- [ ] **业务流程完成后是否有流程级集成测试（`XxxIT`）并通过？**
- [ ] **本地 `mvn clean test` 全部通过 + `mvn clean package -DskipTests` 编译通过？**

---

**详细规则**（如能访问 structure-agent-rules 仓库）：`prompts/structure-boot/developer.md` / `prompts/structure-boot/components.md` / `prompts/structure-boot/tester.md` / `prompts/structure-boot/reviewer.md` / `prompts/structure-boot/architect.md` / `prompts/structure-boot/project-scaffolding.md` / `prompts/structure-boot/validation.md` / `prompts/structure-boot/swagger.md` / `prompts/structure-boot/ci-cd.md` / `CLAUDE.md`。

## _common


> 本文件是 **Codex / 通用 AI Agent** 在 structure-projects 业务项目中的通用工作规则。
> 由 [structure-agent-rules](https://github.com/structure-projects/structure-agent-rules) 仓库的 `_common/codex/AGENTS.md` 模板复制而来。
>
> **使用方式**：安装时自动合并到项目 `AGENTS.md`。
> **详细规则**：`prompts/_common/git.md` / `prompts/_common/version-management.md` / `prompts/_common/documentation.md` / `prompts/_common/naming.md` / `prompts/_common/project-structure.md` 等。

---

## 1. Git 分支管理（MUST）

### 分支模型

```
master ──────────────────────── ●(hotfix merge) ──────
  ↑                 ↑          ↑
develop ────●←feat-A─●←feat-B──●←release-1.2.0──●←fix-C──
            ↑        ↑         ↑                 ↑
          feat-A   feat-B   release-1.2.0      fix-C
```

### 分支命名

| 分支 | 用途 | 来源 | 合并目标 | 生命周期 |
|------|------|------|----------|----------|
| `master` | 生产环境稳定代码 | — | — | 永久 |
| `develop` | 开发主分支 | master | — | 永久 |
| `feat-{描述/版本}` | 功能开发 | develop | develop | 合并后删除 |
| `fix-{描述/版本}` | Bug 修复（开发环境） | develop | develop | 合并后删除 |
| `release-{版本号}` | 发布准备 | develop | master + develop | 合并后删除 |
| `hotfix-{版本号}` | 生产热修复 | master | master + develop | 合并后删除 |

### 核心约束

- **禁止** 直接在 `master` 或 `develop` 上推送代码。
- **禁止** 将 `feat-*` 分支直接合并到 `master`（必须经过 `develop`）。
- **禁止** 在生产热修复分支中夹带新功能。
- **禁止** 在未关联版本号的情况下提交代码。
- **MUST** 已发布的 commit 不可变，不 force push 公共分支。
- **MUST** 所有代码合并到 `develop` 前通过 CI 测试。

## 2. 版本管理（MUST）

### 版本格式

`X.Y.Z` 3 段式语义化版本：

| 段位 | 名称 | 自增时机 | 示例 |
|------|------|----------|------|
| **X** | 架构版本 | 架构级别调整（模块拆分/合并、框架大版本升级） | 1 → 2 |
| **Y** | 功能版本 | 新增功能 | 1.0 → 1.1 |
| **Z** | 修复版本 | Bug 修复（每次修复必增） | 1.1.0 → 1.1.1 |

### 核心约束

- **MUST** 版本号不可重复，不可回退。
- **MUST** 每次开发前确认目标版本号（X/Y/Z 哪段自增）。
- **MUST** Y 自增时 Z 归 0；X 自增时 Y 和 Z 归 0。
- **MUST** 开发阶段使用 `{X}.{Y}.{Z}-SNAPSHOT`，发布时去掉 `-SNAPSHOT`。
- **MUST** 分支命名与版本号对应：`feat-1.2.0` 对应功能版本 `1.2.0`。
- **MUST** 发布前检查 `README.md` 是否与当前版本代码一致。
- **禁止** 在 README 过期的情况下发布版本。

## 3. 文档管理（MUST）

### 文档目录结构

```
docs/
├── overview.md                 # 概要设计
├── features/                   # 详细设计
├── {version}/                  # 版本快照
│   └── changelog/
│       ├── 001.md
│       └── ...
└── README.md
```

### AI 开发前置验证（编码前 MUST 执行）

1. **确认目标版本号**：X/Y/Z 哪段自增？
2. **验证设计文档存在**：`docs/features/` 下是否有对应的详细设计文档？
3. **确认预期交付**：从设计文档提取交付物清单并确认。
4. **禁止**在设计文档不存在或版本号不明确的情况下开始编码。

### Changelog 格式（每次变更 MUST 写入）

`docs/{version}/changelog/{序号}.md`：

```markdown
# 变更 #{序号}
- **类型**: feat / fix
- **日期**: YYYY-MM-DD
- **涉及文件**: xxx.java, xxx.sql, ...
- **原始设计**: [引用详细设计文档]
- **变更内容**: 本次修改的具体内容
- **测试结果**: 通过 / 失败 + 影响范围
- **修改人**: xxx
```

### 红线

- **设计文档缺失** → 禁止编码
- **版本号不明** → 禁止编码
- **changelog 未更新** → 禁止提交

## 4. 命名规范

### 通用命名（MUST）

| 元素 | 规范 | 示例 |
|------|------|------|
| 类名/接口名 | `UpperCamelCase` | `UserService`, `OrderRepository` |
| 方法名/变量名 | `lowerCamelCase` | `findById`, `userName` |
| 常量 | `UPPER_SNAKE_CASE` | `MAX_RETRY_COUNT` |
| 包名 | 全小写无分隔符 | `cn.structured.admin.biz.service` |
| 数据库表/字段 | `lower_snake_case` | `user_role`, `created_at` |
| REST API URL | `kebab-case` | `/api/user-roles` |

### Java 注释规范（MUST）

**类头注释**：

```java
/**
 * 用户管理服务实现
 *
 * @author zhangsan
 * @version 1.2.0
 * @since JDK 17 2025-07-31
 */
```

- **MUST** `@version` 与项目版本号同步更新。
- **SHOULD** `@since` 记录首次创建的 JDK 版本与日期。

**方法注释**：每个 public/protected 方法 MUST 包含 `@param` 和 `@return`。

## 5. 项目结构（MUST）

### 文档目录

```
docs/
├── overview.md                 # 概要设计
├── features/                   # 详细设计
├── {version}/                  # 版本快照
└── README.md                   # 文档索引
```

### 禁止事项

- **禁止** 将生成代码与手写代码混放在同一目录。
- **禁止** 在 commit 中包含临时文件、IDE 配置、构建产物。
- **禁止** 在 `README.md` 中写入超前于代码的内容。

---

**详细规则**（如能访问 structure-agent-rules 仓库）：`prompts/_common/git.md` / `prompts/_common/version-management.md` / `prompts/_common/documentation.md` / `prompts/_common/naming.md` / `prompts/_common/project-structure.md`。

