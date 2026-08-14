---
description: |
triggers:
  - 配置监控
  - 接入 Prometheus
  - 配置告警
  - Grafana
  - 监控接入
  - 接入可观测
role: devops
priority: medium
category: deployment
stack: _common
alwaysApply: false
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
