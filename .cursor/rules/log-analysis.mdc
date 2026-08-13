---
description: |
triggers:
  - 分析日志
  - 查日志
  - 日志排查
  - log analysis
  - 看日志
role: devops
priority: medium
category: support
stack: _common
alwaysApply: false
---


# 日志分析

> 系统性分析日志定位问题。

## 常用命令

### 实时跟随

```bash
# 本地
tail -f logs/application.log

# K8s
kubectl logs -f <pod> -n <ns>

# Docker
docker logs -f <container>
```

### 搜索

```bash
# 按关键字
grep "ERROR" logs/application.log
grep "userId=123" logs/application.log

# 按时间
grep "2026-08-13 10:" logs/application.log

# 按 traceId
grep "traceId=abc123" logs/application.log

# 统计
grep -c "ERROR" logs/application.log
```

### ELK / Loki 查询

```
# Loki LogQL
{app="user-service"} |= "ERROR" |~ "userId=\\d+"
```

## 常见问题模式

### NPE / 空指针

```bash
grep "NullPointerException" logs/application.log -A 20
```

### SQL 慢查询

```bash
grep "slow query" logs/application.log
```

### OOM

```bash
grep "OutOfMemoryError" logs/application.log
```

## 关联

- Wiki：`wiki/_common/observability.md` `wiki/_common/logging.md`
- 相关：`debug-issue` / `kubectl-ops` / `docker-cli`
