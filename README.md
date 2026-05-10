# Structure Message Center

基于插件化架构的消息中心系统，支持多种消息通道（站内消息、短信、邮件、微信、钉钉等），提供灵活的消息发送能力。

## 特性

- **插件化架构**：支持动态扩展消息通道，无需修改核心代码即可添加新通道
- **多通道支持**：内置站内消息、短信、邮件通道，可扩展微信、钉钉等通道
- **异步发送**：支持同步和异步两种发送模式，提升系统性能
- **批量发送**：支持批量消息发送，提高效率
- **重试机制**：自动重试失败消息，支持配置最大重试次数
- **多租户支持**：支持多组织/租户隔离

## 技术栈

- **Java**: 8
- **Spring Boot**: 2.7.18
- **MyBatis Plus**: 3.5.3.1
- **Redis**: 缓存支持
- **Maven**: 依赖管理

## 项目结构

```
structure-message/
├── structure-message-boot/         # 启动模块
│   └── src/main/java/cn/structure/message/starter/
│       ├── configuration/          # 配置类
│       └── MessageCenterApplication.java
├── structure-message-common/       # 公共模块
│   └── src/main/java/com/structure/message/common/
│       ├── constant/               # 常量定义
│       ├── exception/              # 异常类
│       ├── model/                  # 数据模型
│       ├── plugin/                 # 插件接口
│       └── sms/                    # 短信相关
├── structure-message-core/         # 核心模块
│   └── src/main/java/com/structure/message/core/
│       ├── api/                    # REST API
│       ├── config/                 # 配置类
│       ├── mapper/                 # MyBatis Mapper
│       ├── plugin/                 # 插件管理器
│       └── service/                # 业务服务
├── structure-message-plugins/      # 插件模块
│   ├── message-center-plugin-email/    # 邮件插件
│   ├── message-center-plugin-internal/ # 站内消息插件
│   ├── message-center-plugin-sms/      # 短信插件
│   └── structure-message-plugin-api/   # 插件API
├── structure-message-sms-provider/ # 短信服务商
│   ├── structure-message-sms-aliyun-provider/   # 阿里云短信
│   ├── structure-message-sms-huawei-provider/   # 华为短信
│   └── structure-message-sms-tencent-provider/  # 腾讯短信
├── .github/workflows/              # CI/CD配置
├── Dockerfile                      # Docker镜像构建
├── init_channels.sql               # 初始化SQL
├── liveness.sh                     # 健康检查脚本
└── pom.xml                         # Maven配置
```

## 快速开始

### 环境要求

- JDK 8+
- MySQL 5.7+
- Redis 5.0+

### 数据库初始化

执行 `init_channels.sql` 文件初始化消息通道配置：

```sql
mysql -u username -p database_name < init_channels.sql
```

### 配置说明

修改 `structure-message-boot/src/main/resources/application.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/message_center?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
```

### 启动项目

```bash
cd structure-message
mvn clean package -DskipTests
cd structure-message-boot
mvn spring-boot:run
```

## API 接口

### 消息发送

#### 发送单条消息

**POST** `/api/message/send`

请求体：

```json
{
    "orgId": 1,
    "businessId": "ORDER-20240101001",
    "channelCode": "SMS",
    "receiver": "13800138000",
    "content": "您的订单已发货，单号：ORDER-20240101001",
    "priority": 5,
    "maxRetryTimes": 3
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orgId | Long | 否 | 组织ID，默认从租户上下文获取 |
| businessId | String | 否 | 业务ID，用于关联业务数据 |
| channelCode | String | 是 | 通道编码（INTERNAL/SMS/EMAIL） |
| templateCode | String | 否 | 模板编码 |
| receiver | String | 是 | 接收者（手机号/邮箱/用户ID） |
| content | String | 否 | 消息内容（无模板时必填） |
| params | Map | 否 | 模板参数 |
| priority | Integer | 否 | 优先级（1-10），默认5 |
| maxRetryTimes | Integer | 否 | 最大重试次数，默认3 |

#### 异步发送消息

**POST** `/api/message/send/async`

#### 批量发送消息

**POST** `/api/message/send/batch`

#### 重新发送消息

**POST** `/api/message/resend/{messageId}`

### 消息查询

#### 查询消息发送记录

**GET** `/api/message/records?businessId=&channelCode=&status=`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| businessId | String | 否 | 业务ID |
| channelCode | String | 否 | 通道编码 |
| status | Integer | 否 | 状态（0:待发送 1:发送中 2:成功 3:失败） |

#### 获取消息发送状态

**GET** `/api/message/status/{messageId}`

### 通道管理

#### 新增通道

**POST** `/api/message-channel/`

#### 更新通道

**PUT** `/api/message-channel/{id}`

#### 删除通道

**DELETE** `/api/message-channel/{id}`

#### 查询通道列表

**GET** `/api/message-channel/{page}/{pageSize}/page`

## 插件开发

### 实现消息通道插件

实现 `MessageChannelPlugin` 接口：

```java
public class CustomMessagePlugin implements MessageChannelPlugin {
    
    @Override
    public String getChannelCode() {
        return "CUSTOM";
    }
    
    @Override
    public String getChannelName() {
        return "自定义通道";
    }
    
    @Override
    public ChannelType getChannelType() {
        return ChannelType.WECHAT;
    }
    
    @Override
    public void initialize(MessageChannelConfig config) throws MessageException {
        // 初始化配置
    }
    
    @Override
    public MessageResult send(MessageContext context) throws MessageException {
        // 实现消息发送逻辑
        return MessageResult.success();
    }
    
    // 其他方法实现...
}
```

### 注册插件

在 Spring 配置类中注册插件：

```java
@Bean
public MessageChannelPlugin customMessagePlugin() {
    return new CustomMessagePlugin();
}
```

## 短信服务商

支持以下短信服务商：

- **阿里云**：`structure-message-sms-aliyun-provider`
- **华为云**：`structure-message-sms-huawei-provider`
- **腾讯云**：`structure-message-sms-tencent-provider`

## 通道类型

| 类型 | 编码 | 说明 |
|------|------|------|
| 站内消息 | INTERNAL | 系统内部消息推送 |
| 短信消息 | SMS | 手机短信通知 |
| 邮件消息 | EMAIL | 电子邮件通知 |
| 微信消息 | WECHAT | 微信公众号/小程序消息 |
| 钉钉消息 | DINGTALK | 钉钉消息通知 |

## 状态码

| 状态码 | 说明 |
|--------|------|
| 0 | 待发送 |
| 1 | 发送中 |
| 2 | 发送成功 |
| 3 | 发送失败 |

## 许可证

Apache License 2.0

## 贡献

欢迎提交 Issue 和 Pull Request。