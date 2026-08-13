---
name: rollback
description: |
  当用户要求"回滚/回退/撤销发布"时触发。
  按回滚决策树判断回滚方式并执行。

triggers:
  - 回滚
  - 回退
  - 撤销发布
  - rollback
  - revert
  - 版本回退

role: devops
phase: deployment

when-to-use: |
  部署后健康检查失败、关键指标异常、或用户手动触发回滚。
when-not-to-use: |
  - 仅小 bug 且已有 hotfix 方案（用 hotfix-release）

allowed-tools: Bash, Read

related-rules:
  - common-git
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/git.md

produces:
  - 回滚记录 (changes/changelog/)

human-in-the-loop:
  - 回滚 MUST 用户确认

mode: interactive

category: deployment
stack: _common
priority: critical
maturity: stable
version: "0.3.0"
since: "2026-08-13"
---

# 回滚（Rollback）

> 部署失败 / 异常时按决策树执行回滚。**MUST 用户确认回滚操作**。

## 前置条件

- 已部署版本出现以下任一情况：
  - 健康检查失败
  - 关键指标异常（错误率飙升 / 延迟突增 / QPS 暴跌）
  - 用户手动触发回滚

## 执行步骤

### 第 1 步：确认回滚触发条件

| 触发条件 | 来源 | 判断 |
|---|---|---|
| 健康检查失败 | `deployment-verification` 报告 | 服务不可用 |
| 关键指标异常 | Prometheus / Grafana 告警 | 错误率 > 阈值 / 延迟 > 阈值 |
| 用户手动触发 | 用户指令 | 用户判断需回滚 |

**MUST 用户确认是否执行回滚。**

### 第 2 步：回滚决策树

```
触发回滚
   │
   ├─ 能否回滚到上一版本？
   │     │
   │     ├─ 是 → 版本回退（推荐）
   │     │     │
   │     │     └─ 数据库是否兼容？
   │     │           ├─ 是 → 直接回退版本
   │     │           └─ 否 → 需同时数据回滚
   │     │
   │     └─ 否（无可回滚版本）→ 数据回滚
   │           │
   │           └─ 执行数据库迁移脚本回滚
   │
   └─ 是否需要 hotfix？
         ├─ 是 → 回滚后调用 hotfix-release
         └─ 否 → 回滚后归档
```

### 第 3 步：回滚步骤

#### 3.1 确认当前版本

```bash
# 当前运行版本
git describe --tags  # 或 kubectl rollout status
docker ps --format "{{.Image}}"
```

#### 3.2 选择目标版本

```bash
# 列出最近稳定版本
git tag --sort=-version:refname | head -5
# 或查看 changelog
cat changes/changelog/*.md | head -50
```

**MUST 用户确认目标版本。**

#### 3.3 执行回滚

```bash
# 版本回退（按部署平台）
# K8s
kubectl rollout undo deployment/{x} --to-revision={N}

# Docker
docker pull {image}:{target-version}
docker compose up -d

# 传统主机
systemctl stop {service}
# 替换二进制 / 包
systemctl start {service}
```

#### 3.4 验证回滚

| 检查项 | 通过标准 |
|---|---|
| 服务存活 | HTTP `/health` 200 OK |
| 关键接口 | 返回正确响应 |
| 日志 | 无新 ERROR |
| 监控指标 | 恢复正常阈值 |

### 第 4 步：回滚后处理

#### 4.1 创建 retrospective.md

写入 `changes/proposals/<id>/retrospective.md`（或 `changes/changelog/rollback-{version}.md`）：

```markdown
# 回滚记录

| 字段 | 值 |
|---|---|
| 回滚时间 | YYYY-MM-DD HH:MM |
| 失败版本 | X.Y.Z |
| 目标版本 | X.Y.(Z-1) |
| 回滚原因 | <健康检查失败 / 指标异常 / ...> |
| 回滚人 | <user> |

## 回滚过程
<步骤记录>

## 影响评估
<受影响用户 / 业务 / 时长>

## 后续动作
- [ ] 触发 hotfix（如需要）
- [ ] 根因分析
- [ ] 补充测试用例
```

#### 4.2 通知相关人员

- 通知开发团队回滚原因
- 通知业务方影响范围
- 如需修复 → 调用 `hotfix-release`

## 完成标准

- 回滚到目标版本
- 健康检查全部通过
- 监控指标恢复正常
- 回滚记录已写入
- 相关人员已通知

## 关联

- 前置：`deployment-verification`（健康检查失败触发）
- 相关：`hotfix-release`（回滚后修复）/ `ci-gate`
- Wiki：`wiki/_common/git.md`
