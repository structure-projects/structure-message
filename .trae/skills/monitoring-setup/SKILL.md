---
name: monitoring-setup
description: |
  当用户要求"配置监控/接入 Prometheus/配置告警/接入 Grafana"时触发。
  为服务配置监控、指标暴露、告警规则。

triggers:
  - 配置监控
  - 接入 Prometheus
  - 配置告警
  - Grafana
  - 监控接入
  - 接入可观测

role: devops
phase: deployment

when-to-use: |
  为服务配置监控指标、告警规则、仪表盘。
when-not-to-use: |
  - 仅查询监控（用 kubectl-ops / 直接查 Grafana）
  - 仅修复告警

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - common-logging
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/observability.md

produces:
  - Prometheus 配置
  - 告警规则
  - Grafana Dashboard

requires:
  - skill: deployment-verification
    condition: 服务已部署

trust-level: standard

require-confirm:
  - 告警阈值 MUST 用户确认

mode: assist

category: deployment
stack: _common
priority: medium
version: "0.3.0"
since: "2026-08-13"
---

# 监控接入

> 配置 Prometheus + Grafana + 告警。

## 执行步骤

### 第 1 步：暴露 Prometheus 端点

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    prometheus:
      enabled: true
```

### 第 2 步：配置 Prometheus 抓取

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'user-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['user-service:8080']
```

### 第 3 步：配置告警规则

```yaml
# alert-rules.yml
groups:
- name: service
  rules:
  - alert: ServiceDown
    expr: up == 0
    for: 1m
    labels:
      severity: critical
```

### 第 4 步：Grafana Dashboard

导入或创建 Dashboard。

### 第 5 步：验证

```bash
# 检查端点
curl http://localhost:8080/actuator/prometheus

# 检查 Prometheus 抓取
curl http://prometheus:9090/api/v1/targets
```

## 关键约束

- ✅ **MUST** 暴露 `/actuator/prometheus`
- ✅ **MUST** 配置关键告警
- ❌ **MUST NOT** 在生产环境 100% trace 采样

## 关联

- Wiki：`wiki/_common/observability.md`
- 相关：`deployment-verification`
