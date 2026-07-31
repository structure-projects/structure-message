# 消息中心设计 (structure-message)

> **所属模块**：`structure-message` | 端口：18001 | context-path: /message | 数据库: message_db
> **引用总体设计**：`docs/OVERVIEW_DESIGN.md`
> **基于实际代码版本**：无 Flyway 迁移文件（代码中的 Entity 定义为准）

---

## 1. 模块职责

消息中心负责消息的发送、接收、渠道管理、消息记录管理。

| 功能模块 | 说明 |
|---------|------|
| 消息发送 | 按渠道发送消息（站内信、短信、邮件、APP推送） |
| 消息记录 | 消息发送记录、投递状态 |
| 渠道管理 | 消息渠道配置（短信、邮件、推送通道） |
| 站内信 | 内部消息通知 |
| 消息附件 | 消息附件文件管理 |

---

## 2. 数据库设计

### 2.1 数据库：message_db

### 2.2 核心 Entity 清单（无 Flyway SQL，以代码 Entity 定义为准）

| Entity | 对应表名 | 说明 |
|--------|---------|------|
| MessageRecordPO | `message_record` | 消息发送记录 |
| MessageChannelPO | `message_channel` | 消息渠道定义 |
| InternalMessagePO | `internal_message` | 站内信消息 |
| MessageAccessoryPO | `message_accessory` | 消息附件 |
| OrgChannelConfigPO | `org_channel_config` | 组织级渠道配置 |

### 2.3 message_record（消息发送记录）

核心字段（基于实际 PO 定义）：
- `id`, `message_id` (消息唯一ID), `message_type` (消息类型), `message_title`, `message_content`
- `channel_code` (渠道代码: SMS/EMAIL/PUSH/INTERNAL), `channel_name`
- `sender_id`, `sender_name`, `receiver_id`
- `send_status` (发送状态: PENDING/SENDING/SUCCESS/FAILED)
- `retry_count`, `error_message`
- `biz_type` (业务类型), `biz_id` (业务ID，关联业务单号)
- `tenant_id`, `create_time`, `update_time`, `is_deleted`

### 2.4 message_channel（消息渠道定义）

核心字段：
- `id`, `channel_code` (唯一), `channel_name`
- `channel_type` (SMS/EMAIL/PUSH/WECHAT/DINGTALK)
- `provider` (服务商: ALIYUN/TENCENT/SMTP/CUSTOM)
- `config_json` (渠道配置 JSON，含 API Key、Secret、模板等)
- `status`, `description`, `tenant_id`, `create_time`, `update_time`

### 2.5 internal_message（站内信）

核心字段：
- `id`, `message_id` (关联发送记录)
- `sender_id`, `receiver_id`
- `title`, `content`, `message_type` (SYSTEM/NOTICE/BUSINESS)
- `is_read`, `read_time`
- `link_url` (跳转链接)
- `tenant_id`, `create_time`, `update_time`, `is_deleted`

### 2.6 message_accessory（消息附件）

核心字段：
- `id`, `message_id` (关联消息记录)
- `file_name`, `file_url`, `file_size`, `file_type`
- `create_time`

### 2.7 org_channel_config（组织渠道配置）

核心字段：
- `id`, `organization_id` (关联组织)
- `channel_id` (关联渠道)
- `enabled` (是否启用), `priority` (优先级)
- `config_override` (配置覆盖 JSON)
- `tenant_id`, `create_time`, `update_time`

---

## 3. API 设计（基于实际 Controller）

### MessageChannelEndpoint — `@RequestMapping("/message/channel")`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/message/channel/list` | 渠道列表 |
| POST | `/message/channel` | 创建渠道 |
| PUT | `/message/channel/{id}` | 更新渠道配置 |
| DELETE | `/message/channel/{id}` | 删除渠道 |
| GET | `/message/channel/{id}` | 渠道详情 |

### MessageChannelConfigEndpoint — `@RequestMapping("/message/channel-config")`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/message/channel-config/list/{orgId}` | 组织渠道配置列表 |
| POST | `/message/channel-config` | 创建/更新组织渠道配置 |

### MessageEndpoint — `@RequestMapping("/message/message")`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/message/message/send` | 发送消息（同步/异步） |
| GET | `/message/message/record/{id}` | 查询消息发送记录 |
| GET | `/message/message/records` | 分页查询发送记录 |

### InternalMessageController — `@RequestMapping("/message/internal")`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/message/internal/list` | 站内信列表（当前用户） |
| GET | `/message/internal/unread-count` | 未读站内信数量 |
| PUT | `/message/internal/{id}/read` | 标记已读 |
| PUT | `/message/internal/read-all` | 全部标为已读 |

---

## 4. 消息发送流程

### 4.1 消息发送架构

```
业务服务 → Event (RabbitMQ) → EventConsumer → 消息处理
                                            ├── 站内信 → internal_message 表
                                            ├── 短信 → 短信渠道 provider
                                            ├── 邮件 → 邮件渠道 provider
                                            └── 推送 → APP推送渠道 provider
```

### 4.2 消息渠道

```
message_channel
├── SMS:     (阿里云短信 / 腾讯云短信)
├── EMAIL:   (SMTP / 腾讯企业邮)
├── PUSH:    (个推 / 极光推送)
├── WECHAT:  (公众号模板消息 / 小程序订阅消息)
└── DINGTALK: (钉钉机器人 / 钉钉通知)
```

### 4.3 消息重试机制

- 发送失败自动重试（retry_count 计数）
- 超过最大重试次数标记为 FAILED
- 失败消息可通过人工重发接口重新发送

---

## 5. 跨模块集成

### 5.1 消息生产者（RabbitMQ 发布者）

| 业务服务 | 消息类型 | 说明 |
|---------|---------|------|
| auth-service | 登录通知 | 异地登录告警 |
| org-service | 邀请通知 | 成员邀请消息 |
| org-service | 审批通知 | 加入申请审批结果 |
| tenant-service | 租户通知 | 租户创建、套餐变更 |
| audit-service | 审计告警 | 异常操作告警 |

### 5.2 消息消费者（RabbitMQ）

message-service 作为消费者监听 `message.send` 队列：

```
EventConsumer:
  └── handle(SendMessageEvent)
       ├── 解析消息事件
       ├── 根据渠道路由
       ├── 调用对应 provider 发送
       └── 记录 message_record
```

### 5.3 对外 API

| 端点 | 方法 | 说明 |
|------|------|------|
| /message/message/send | POST | 发送消息（其他服务调用） |
| /message/internal/unread-count | GET | 获取未读站内信数 |
| /message/internal/list | GET | 获取站内信列表 |

---

## 6. 待确认清单

| # | 不一致项 | 当前代码 | 原设计意图 | 建议 |
|---|---------|---------|-----------|------|
| 1 | Flyway 迁移 | 无 Flyway SQL 文件 | 应通过 Flyway 管理表结构 | **待确认**：需为 message_db 创建 Flyway 迁移文件 |
| 2 | Entity 定义不完整 | PO 类缺少完整字段注解 | 需要完整 DDL | **待确认**：基于 Entity PO 补全 DDL，确认表名和字段名 |
| 3 | 消息模板 | 无消息模板表 | bak 文档提到消息模板 | **待确认**：消息模板功能是否需要？在 message 模块还是独立实现？ |
| 4 | 消息推送计划 | 无定时推送/计划推送功能 | 设计提到"消息推送计划" | **待确认**：是否需要在当前版本实现？ |
| 5 | 消费者实现 | EventConsumer 代码存在 | 事件驱动解耦 | 消费者代码已实现，需确认 RabbitMQ 队列命名规范 |
