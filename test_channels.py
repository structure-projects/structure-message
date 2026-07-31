import requests
import json
import time
from datetime import datetime

BASE_URL = "http://localhost:8080"

CHANNEL_CONFIGS = [
    {
        "name": "邮件-QQ邮箱",
        "channelCode": "EMAIL",
        "configName": "mail",
        "test_message": {
            "channelCode": "EMAIL",
            "receiver": "361648887@qq.com",
            "content": "<h1>邮件测试</h1><p>这是QQ邮箱测试 - " + datetime.now().strftime("%Y-%m-%d %H:%M:%S") + "</p>",
            "businessSource": "TEST",
            "configName": "mail",
            "params": {"subject": "测试邮件-QQ邮箱"}
        }
    },
    {
        "name": "邮件-主配置",
        "channelCode": "EMAIL",
        "configName": "primary",
        "test_message": {
            "channelCode": "EMAIL",
            "receiver": "361648887@qq.com",
            "content": "<h1>邮件测试</h1><p>这是主配置测试 - " + datetime.now().strftime("%Y-%m-%d %H:%M:%S") + "</p>",
            "businessSource": "TEST",
            "configName": "primary",
            "params": {"subject": "测试邮件-主配置"}
        }
    },
    {
        "name": "邮件-备用配置",
        "channelCode": "EMAIL",
        "configName": "secondary",
        "test_message": {
            "channelCode": "EMAIL",
            "receiver": "361648887@qq.com",
            "content": "<h1>邮件测试</h1><p>这是备用配置测试 - " + datetime.now().strftime("%Y-%m-%d %H:%M:%S") + "</p>",
            "businessSource": "TEST",
            "configName": "secondary",
            "params": {"subject": "测试邮件-备用配置"}
        }
    },
    {
        "name": "短信-阿里云",
        "channelCode": "SMS",
        "configName": "aliyun",
        "test_message": {
            "channelCode": "SMS",
            "receiver": "17624018021",
            "content": "【测试】您的验证码是123456，有效期5分钟。",
            "businessSource": "TEST",
            "configName": "aliyun"
        }
    },
    {
        "name": "短信-腾讯云",
        "channelCode": "SMS",
        "configName": "tencent",
        "test_message": {
            "channelCode": "SMS",
            "receiver": "17624018021",
            "content": "【测试】您的验证码是654321，有效期5分钟。",
            "businessSource": "TEST",
            "configName": "tencent"
        }
    },
    {
        "name": "短信-华为云",
        "channelCode": "SMS",
        "configName": "huawei",
        "test_message": {
            "channelCode": "SMS",
            "receiver": "17624018021",
            "content": "【测试】您的验证码是111111，有效期5分钟。",
            "businessSource": "TEST",
            "configName": "huawei"
        }
    },
    {
        "name": "IM-钉钉应用",
        "channelCode": "IM",
        "configName": "dingtalk-app",
        "test_message": {
            "channelCode": "IM",
            "receiver": "03495912400720848690",
            "content": "【测试】钉钉应用消息 - " + datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            "businessSource": "TEST",
            "configName": "dingtalk-app",
            "params": {"messageType": "P2P", "title": "测试消息-钉钉应用"}
        }
    },
    {
        "name": "IM-飞书(用户)",
        "channelCode": "IM",
        "configName": "feishu",
        "test_message": {
            "channelCode": "IM",
            "receiver": "ou_aabc625e2b124ac72fcd93d1e897cfa4",
            "content": "【测试】飞书IM消息 - " + datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            "businessSource": "TEST",
            "configName": "feishu",
            "params": {"messageType": "P2P", "title": "测试消息-飞书"}
        }
    },
    {
        "name": "IM-钉钉(群聊)",
        "channelCode": "IM",
        "configName": "dingtalk",
        "test_message": {
            "channelCode": "IM",
            "receiver": "03495912400720848690",
            "content": "【测试】钉钉群聊消息 - " + datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            "businessSource": "TEST",
            "configName": "dingtalk",
            "params": {"messageType": "BOT", "title": "测试消息-钉钉群聊"}
        }
    },
    {
        "name": "IM-飞书(群聊)",
        "channelCode": "IM",
        "configName": "feishu",
        "test_message": {
            "channelCode": "IM",
            "receiver": "oc_a9fac1c03508acd4c3d16390f9fde3c7",
            "content": "【测试】飞书群聊消息 - " + datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            "businessSource": "TEST",
            "configName": "feishu",
            "params": {"messageType": "BOT", "title": "测试消息-飞书群聊"}
        }
    }
]

def log(message, level="INFO"):
    print(f"[{level}] {message}")

def send_message(message):
    try:
        url = f"{BASE_URL}/api/message/send"
        data = {
            "orgId": 1,
            "channelCode": message["channelCode"],
            "receiver": message["receiver"],
            "content": message["content"],
            "businessSource": message["businessSource"]
        }
        if message.get("configName"):
            data["configName"] = message["configName"]
        if "params" in message:
            data["params"] = message["params"]

        response = requests.post(url, json=data)
        response.raise_for_status()
        result = response.json()
        if result.get("success"):
            data = result.get("data", {})
            success = data.get("success", False)
            if success:
                log(f"✅ 发送成功 - {message['channelCode']}/{message.get('configName', 'default')} (消息ID: {data.get('messageId')})", "SUCCESS")
                return {"success": True, "messageId": data.get("messageId")}
            else:
                log(f"⚠️ 发送失败 - {message['channelCode']}/{message.get('configName', 'default')}: {data.get('errorMsg')}", "WARN")
                return {"success": False, "errorMsg": data.get("errorMsg")}
        else:
            log(f"❌ 发送失败 - {message['channelCode']}/{message.get('configName', 'default')}: {result.get('message')}", "ERROR")
            return {"success": False, "errorMsg": result.get("message")}
    except Exception as e:
        log(f"❌ 发送异常 - {message['channelCode']}/{message.get('configName', 'default')}: {str(e)}", "ERROR")
        return {"success": False, "errorMsg": str(e)}

def test_all_channels():
    log("="*70)
    log("🚀 开始测试消息中心渠道配置与发送功能", "INFO")
    log("="*70)

    results = []

    log("\n📤 阶段一：测试各渠道消息发送", "INFO")
    log("-"*70)

    for config in CHANNEL_CONFIGS:
        config_name = config.get("configName") or "default"
        log(f"\n测试: {config['name']} ({config['channelCode']}/{config_name})", "INFO")
        log(f"   接收者: {config['test_message']['receiver']}", "INFO")
        result = send_message(config["test_message"])
        results.append({
            "name": config["name"],
            "channelCode": config["channelCode"],
            "configName": config.get("configName"),
            "receiver": config["test_message"]["receiver"],
            "send_success": result["success"],
            "messageId": result.get("messageId"),
            "send_error": result.get("errorMsg")
        })
        time.sleep(0.5)

    log("\n" + "="*70)
    log("📊 测试报告", "INFO")
    log("="*70)

    print("""
┌──────────────────┬─────────────┬────────────┬──────────┬─────────────┐
│       渠道名称        │  渠道编码   │  配置名称  │ 发送状态 │   消息ID    │
├──────────────────┼─────────────┼────────────┼──────────┼─────────────┤""")

    total = len(results)
    success_count = 0

    channel_groups = {}
    for result in results:
        channel = result["channelCode"]
        if channel not in channel_groups:
            channel_groups[channel] = []
        channel_groups[channel].append(result)

    for channel, items in channel_groups.items():
        first_row = True
        for item in items:
            config_display = item["configName"] or "default"
            status_icon = "✅" if item["send_success"] else "❌"
            if item["send_success"]:
                success_count += 1

            row_name = item["name"] if first_row else ""
            print(f"│ {row_name:^16} │ {item['channelCode']:^11} │ {config_display:^10} │ {status_icon:^8} │ {str(item.get('messageId') or 'N/A'):^11} │")

            if not item["send_success"] and item["send_error"]:
                error_msg = (item["send_error"] or "")[:40]
                print(f"│                  │             │            │          │ {error_msg:^11} │")
            first_row = False

        print("├──────────────────┼─────────────┼────────────┼──────────┼─────────────┤")

    print("└──────────────────┴─────────────┴────────────┴──────────┴─────────────┘")

    log(f"\n📈 统计结果:", "INFO")
    log(f"   测试总数: {total}, 成功: {success_count}, 失败: {total - success_count}", "INFO")
    success_rate = (success_count / total * 100) if total > 0 else 0
    log(f"   成功率: {success_rate:.1f}%", "INFO")

    log(f"\n📋 各渠道状态:", "INFO")
    for result in results:
        icon = "✅" if result["send_success"] else "❌"
        status = "成功" if result["send_success"] else f"失败({result['send_error'][:30] if result['send_error'] else ''})"
        log(f"   {icon} {result['channelCode']}/{result['name']}: {status}", "INFO")

    if success_count == total:
        log("\n🎉 所有渠道测试通过！", "SUCCESS")
    else:
        log(f"\n⚠️ 部分渠道测试失败 ({total - success_count}个)", "WARN")

if __name__ == "__main__":
    test_all_channels()