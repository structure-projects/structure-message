---
description: |
triggers:
  - 集成测试
  - IT
  - 跨模块测试
  - Testcontainers
  - 集成测试用例
  - integration test
role: tester
priority: high
category: testing
stack: _common
alwaysApply: false
---


# 集成测试

> 跨模块 / 跨服务测试。**MUST 用 Testcontainers 真实中间件**。

## 与单测的边界

| 类型 | 范围 | 工具 |
|---|---|---|
| **单测** | 函数级 / 类级 | JUnit + Mockito |
| **集成测试** | 跨模块 / DB / MQ / Redis | Testcontainers |
| **E2E** | 端到端用户场景 | Playwright / Cypress |

## 核心原则

- ✅ **MUST** 用 Testcontainers（真实 DB / MQ / Redis）
- ❌ **MUST NOT** 用 H2 替代 MySQL（行为不一致）
- ❌ **MUST NOT** 用内存 MQ 替代 RocketMQ / Kafka

## Testcontainers 示例

### MySQL

```java
@SpringBootTest
@Testcontainers
class UserServiceIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("test")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private IUserService userService;

    @Test
    void shouldCreateUser() {
        // 真实 MySQL 环境测试
    }
}
```

### Redis

```java
@Container
@ServiceConnection
static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
    .withExposedPorts(6379);
```

### RocketMQ / Kafka

```java
@Container
static KafkaContainer kafka = new KafkaContainer(
    DockerImageName.parse("confluentinc/cp-kafka:latest")
);
```

## 关键约束

- ✅ **MUST** 用 `@Testcontainers` + `@Container`
- ✅ **MUST** 用 `@ServiceConnection`（Spring Boot 3.1+）
- ✅ **MUST** 测试后清理数据
- ❌ **MUST NOT** 用 `@MockBean` 替代真实中间件

## 关联

- 前置：`coding`
- Wiki：`wiki/_common/testing-strategies.md`
- 相关：`unit-testing` / `e2e-testing`
