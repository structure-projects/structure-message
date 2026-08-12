# structure-message 消息中心

统一消息中心服务，负责站内信、短信、IM、邮件等多渠道消息的发送、配置与记录管理。基于 DDD（领域驱动设计）分层架构组织，并通过插件化（plugins）与供应商（provider）扩展机制支持多渠道灵活接入。

- **GroupId / ArtifactId**：`cn.structured:structure-message`
- **版本**：`2.0.0-SNAPSHOT`
- **父 POM**：`cn.structured:structure-dependencies:1.5.0`
- **基础包路径**：`cn.structured.message`

## 技术栈

| 组件 | 版本 | 说明 |
| --- | --- | --- |
| Spring Boot | 4.0.6 | 主框架（Jakarta EE） |
| JDK | 17 | 编译与运行 |
| Spring Cloud | 2025.1.0 | 微服务套件 |
| Spring Cloud Alibaba | 2025.1.0.0 | Nacos 注册/配置中心 |
| MyBatis-Plus | 3.5.16 | ORM（`mybatis-plus-spring-boot4-starter`） |
| SpringDoc OpenAPI | 3.0.3 | 接口文档 |
| RabbitMQ | - | 消息广播与事件总线 |
| Redis | - | 站内信存储 / 缓存 |
| Freemarker | 2.3.34 | 消息模板渲染 |
| Testcontainers | 1.20.6 | 集成测试 |
| structure-infra | 1.3.1 | 基础设施组件 |
| structure-security | 1.1.5 | 安全鉴权 |
| structure-tenant | 1.4.3 | 多租户 |
| structure-datascope | 1.0.3 | 数据权限 |

## 模块结构（DDD 分层 + 插件/供应商扩展）

```
structure-message
├── structure-message-dependencies       # 父 POM，统一依赖与版本管理（聚合模块）
├── structure-message-common             # 公共层：DTO/VO/常量/异常、SMS & IM Provider 接口、插件接口
├── structure-message-domain             # 领域层：实体、领域事件、仓储接口、领域服务、插件管理
├── structure-message-infra              # 基础设施层：仓储实现、事件发布、插件实现、重试调度器、Redis 存储
├── structure-message-plugins            # 插件聚合：plugin-api、internal、sms、email、im
├── structure-message-sms-provider       # 短信供应商：aliyun、tencent
├── structure-message-im-provider        # IM 供应商：feishu、dingtalk、wechatwork、wechat、netease
├── structure-message-repository-mybatis # MyBatis-Plus 持久化：Mapper/PO/Delegate
├── structure-message-application        # 应用层：Assembler、应用服务
├── structure-message-interfaces         # 接口层：Controller、事件 Consumer
└── structure-message-boot               # 启动模块：MessageCenterApplication 启动入口
```

依赖方向：`common → domain → infra → repository-mybatis`，`application → domain + infra`，`interfaces → application`，`boot → all`。`plugins` 与 `sms-provider` / `im-provider` 作为可插拔扩展，按需引入。

## 环境要求

- JDK 17
- Maven 3.9+
- MySQL 8.0+
- Redis 7+
- RabbitMQ 3.x（消息与事件通道）
- Nacos 2.x（注册与配置中心，可选）

## 快速启动

1. 准备 MySQL、Redis、RabbitMQ 中间件。
2. 在 `structure-message-boot` 模块下执行：

   ```bash
   mvn clean package -DskipTests
   java -jar structure-message-boot/target/message-service.jar
   ```

3. 默认激活 `dev` profile，从 classpath 加载 `message-service.yaml`；生产环境使用 `pro` profile 并从 Nacos 拉取配置。

## 端口配置

- 服务名：`message-service`
- 默认端口：`8080`
- `dev` 环境端口：`18001`
- `pro` 环境管理端口：`7777`
- 接口文档：`http://localhost:18001/swagger-ui.html`，API 分组 `message`
