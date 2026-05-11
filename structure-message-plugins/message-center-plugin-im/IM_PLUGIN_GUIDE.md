# IM消息插件使用指南

## 概述

IM消息插件支持多种即时通讯渠道，包括点对点(P2P)用户消息、自定义群机器人消息和应用机器人群聊消息。

### 支持的渠道

| 渠道 | Provider | 点对点(P2P) | 自定义机器人 | 应用机器人群聊 |
|------|----------|-------------|-------------|---------------|
| 飞书 | feishu | ✅ | ✅ | ✅ |
| 钉钉 | dingtalk | ✅ | ✅ | ❌ |
| 企业微信 | wechatwork | ✅ | ✅ | ❌ |
| 网易云信 | netease | ✅ | ❌ | ❌ |

---

## 数据库配置

### 1. 添加通道记录

```sql
-- 插入IM通道记录到 channel 表
INSERT INTO channel (channel_code, channel_name, channel_type, plugin_class, status)
VALUES ('IM', 'IM消息通道', 'IM', 'com.structure.message.plugin.im.ImMessagePlugin', 1);
```

### 2. 添加组织通道配置

```sql
-- 插入组织通道配置到 org_channel_config 表
-- channel_id 需要替换为上面插入的通道ID (通常为4)
INSERT INTO org_channel_config (org_id, channel_id, config_value, status)
VALUES (1, 4, '{"provider":"feishu","appId":"your-app-id","appSecret":"your-app-secret","webhookUrl":"your-webhook-url","signSecret":"your-sign-secret","botId":"your-bot-id"}', 1);
```

---

## 各渠道配置说明

### 飞书 (Feishu)

```json
{
  "provider": "feishu",
  "appId": "飞书应用AppID",
  "appSecret": "飞书应用AppSecret",
  "webhookUrl": "飞书群机器人Webhook地址(可选，用于自定义机器人消息)",
  "signSecret": "签名密钥(可选，用于群机器人签名验证)",
  "botId": "机器人ID(可选)"
}
```

### 钉钉 (Dingtalk)

```json
{
  "provider": "dingtalk",
  "appId": "钉钉应用AppKey",
  "appSecret": "钉钉应用AppSecret",
  "webhookUrl": "钉钉群机器人Webhook地址(可选，用于自定义机器人消息)",
  "signSecret": "签名密钥(可选，用于群机器人签名验证)",
  "botId": "机器人AgentId(可选)"
}
```

### 企业微信 (Wechat Work)

```json
{
  "provider": "wechatwork",
  "appId": "企业微信corpid",
  "appSecret": "企业微信corpsecret",
  "webhookUrl": "企业微信群机器人Webhook地址(可选，用于自定义机器人消息)",
  "botId": "应用AgentId(可选)"
}
```

### 网易云信 (Netease IM)

```json
{
  "provider": "netease",
  "appId": "云信应用AppKey",
  "appSecret": "云信应用AppSecret",
  "botId": "发送方账号(可选)"
}
```

---

## API调用示例

### 基础调用

```bash
curl -X POST http://localhost:8080/api/message/send \
  -H "Content-Type: application/json" \
  -d '{
    "channelCode": "IM",
    "receiver": "接收者ID",
    "content": "消息内容",
    "businessSource": "TEST",
    "provider": "feishu"
  }'
```

---

## 飞书 (Feishu) 详细使用说明

### 1. P2P点对点消息 - 应用身份

```bash
curl -X POST http://localhost:8080/api/message/send \
  -H "Content-Type: application/json" \
  -d '{
    "channelCode": "IM",
    "receiver": "ou_xxxxxxxxxxxxxxxxxxxxx",
    "content": "这是一条飞书P2P点对点消息",
    "businessSource": "TEST",
    "provider": "feishu"
  }'
```

- receiver: 支持 open_id (ou_开头) 和 user_id (纯数字)

### 2. P2P点对点消息 - 用户身份

```bash
curl -X POST http://localhost:8080/api/message/send \
  -H "Content-Type: application/json" \
  -d '{
    "channelCode": "IM",
    "receiver": "ou_xxxxxxxxxxxxxxxxxxxxx",
    "content": "这是一条飞书P2P点对点消息",
    "businessSource": "TEST",
    "provider": "feishu",
    "extra": {
      "userAccessToken": "用户访问令牌"
    }
  }'
```

### 3. 自定义群机器人消息 (Webhook)

```bash
curl -X POST http://localhost:8080/api/message/send \
  -H "Content-Type: application/json" \
  -d '{
    "channelCode": "IM",
    "receiver": "任意值",
    "content": "这是一条自定义机器人消息",
    "businessSource": "TEST",
    "provider": "feishu",
    "params": {
      "messageType": "BOT",
      "title": "机器人消息标题"
    }
  }'
```

### 4. 应用机器人群聊消息

```bash
curl -X POST http://localhost:8080/api/message/send \
  -H "Content-Type: application/json" \
  -d '{
    "channelCode": "IM",
    "receiver": "oc_xxxxxxxxxxxxxxxxxxxxx",
    "content": "这是一条应用机器人群聊消息",
    "businessSource": "TEST",
    "provider": "feishu",
    "params": {
      "messageType": "BOT",
      "title": "通知"
    }
  }'
```

- receiver: 群聊的 chat_id (oc_开头)
- 必须确保应用已加入该群聊

### 5. 应用机器人群聊消息 - 带@人

```bash
curl -X POST http://localhost:8080/api/message/send \
  -H "Content-Type: application/json" \
  -d '{
    "channelCode": "IM",
    "receiver": "oc_xxxxxxxxxxxxxxxxxxxxx",
    "content": "这是一条带@的消息，",
    "businessSource": "TEST",
    "provider": "feishu",
    "params": {
      "messageType": "BOT",
      "title": "通知",
      "atUserIds": "ou_xxxxxxxxxxxxxxxxxxxxx"
    }
  }'
```

- atUserIds: 要@的用户ID，支持多个用户用逗号分隔
- 目前需要在代码中配置 @ 的用户名

---

## 钉钉 (Dingtalk) 使用示例

### 1. P2P点对点消息

```bash
curl -X POST http://localhost:8080/api/message/send \
  -H "Content-Type: application/json" \
  -d '{
    "channelCode": "IM",
    "receiver": "userid123",
    "content": "这是一条钉钉P2P点对点消息",
    "businessSource": "TEST",
    "provider": "dingtalk",
    "params": {
      "messageType": "P2P"
    }
  }'
```

### 2. 自定义群机器人消息

```bash
curl -X POST http://localhost:8080/api/message/send \
  -H "Content-Type: application/json" \
  -d '{
    "channelCode": "IM",
    "receiver": "user-id",
    "content": "这是一条钉钉机器人消息",
    "businessSource": "TEST",
    "provider": "dingtalk",
    "params": {
      "messageType": "BOT",
      "title": "机器人消息标题"
    }
  }'
```

---

## 企业微信 (Wechat Work) 使用示例

### 1. P2P点对点消息

```bash
curl -X POST http://localhost:8080/api/message/send \
  -H "Content-Type: application/json" \
  -d '{
    "channelCode": "IM",
    "receiver": "WangWu",
    "content": "这是一条企业微信P2P点对点消息",
    "businessSource": "TEST",
    "provider": "wechatwork",
    "params": {
      "messageType": "P2P"
    }
  }'
```

### 2. 自定义群机器人消息

```bash
curl -X POST http://localhost:8080/api/message/send \
  -H "Content-Type: application/json" \
  -d '{
    "channelCode": "IM",
    "receiver": "user-id",
    "content": "这是一条企业微信机器人消息",
    "businessSource": "TEST",
    "provider": "wechatwork",
    "params": {
      "messageType": "BOT",
      "title": "机器人消息标题"
    }
  }'
```

---

## 网易云信 (Netease IM) 使用示例

### 1. P2P点对点消息

```bash
curl -X POST http://localhost:8080/api/message/send \
  -H "Content-Type: application/json" \
  -d '{
    "channelCode": "IM",
    "receiver": "acc_id_123",
    "content": "这是一条网易云信P2P点对点消息",
    "businessSource": "TEST",
    "provider": "netease",
    "params": {
      "messageType": "P2P"
    }
  }'
```

---

## 参数说明

### MessageContext 参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| channelCode | String | 是 | 通道编码，固定为 `IM` |
| receiver | String | 是 | 接收者ID (用户ID或群聊ID) |
| content | String | 是 | 消息内容 |
| businessSource | String | 是 | 业务来源 |
| provider | String | 否 | 服务提供商，不填则使用默认提供商 |
| subject | String | 否 | 消息标题 |
| extra | Map | 否 | 扩展参数，如 userAccessToken |

### params 参数

| 参数 | 类型 | 说明 |
|------|------|------|
| messageType | String | 消息类型：`P2P`(点对点) 或 `BOT`(机器人) |
| title | String | 消息标题 |
| atUserIds | String | 要@的用户ID，多个用逗号分隔 (飞书专用) |
| url | String | 链接地址(可选) |
| imageUrl | String | 图片地址(可选) |

---

## 默认提供商配置

在 `application.yml` 中配置默认IM提供商：

```yaml
structure:
  message:
    im:
      default-provider: feishu  # 可选值: feishu, dingtalk, wechatwork, netease
```

---

## 消息类型说明

### P2P点对点消息
- 通过应用凭证(appId/appSecret)获取访问令牌
- 使用访问令牌调用开放API发送消息
- 需要用户授权，获取用户的user_id或open_id

### 自定义群机器人消息
- 通过预配置的webhook地址发送消息
- 无需用户授权，适用于群组消息推送
- 支持富文本消息(带标题)

### 应用机器人群聊消息
- 以应用身份发送消息到指定群聊
- 群聊ID通过receiver参数动态传入 (oc_开头)
- 需要确保应用已加入该群聊
- 支持@群成员

---

## 注意事项

1. **凭证安全**：生产环境中请勿将凭证硬编码在代码中，应使用环境变量或配置中心
2. **webhook配置**：自定义机器人消息需要提前在对应平台创建群机器人并获取webhook地址
3. **应用机器人配置**：确保应用已加入目标群聊，否则无法发送群聊消息
4. **群聊选择**：群聊ID通过receiver参数动态传入，支持在运行时选择不同群聊
5. **消息频率**：各平台对消息发送频率有限制，请遵守平台规范
6. **错误处理**：生产环境请根据返回的errorCode进行相应处理和重试
