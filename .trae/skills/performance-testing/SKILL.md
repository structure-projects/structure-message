---
name: performance-testing
description: |
  当用户要求"性能测试/压力测试/负载测试/JMeter/K6/Gatling"时触发。
  编写性能测试，验证系统在高负载下的表现。

triggers:
  - 性能测试
  - 压力测试
  - 负载测试
  - JMeter
  - K6
  - Gatling
  - performance test
  - load test

role: tester
phase: testing

when-to-use: |
  需要验证系统性能（QPS / 延迟 / 资源使用）。
when-not-to-use: |
  - 仅功能测试
  - 仅生产监控

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - common-testing
  - common-performance

reads-before-action:
  - wiki/_common/performance.md
  - wiki/_common/testing-strategies.md

produces:
  - 性能测试脚本
  - 性能测试报告（QPS / P95 / P99 / 资源使用）

requires:
  - skill: integration-testing
    condition: 集成测试已通过

trust-level: standard

require-confirm:
  - 生产环境压测 MUST 用户确认

mode: auto

category: testing
stack: _common
priority: medium
version: "0.3.0"
since: "2026-08-13"
---

# 性能测试

> 验证系统性能。**生产压测 MUST 用户确认**。

## 工具选择

| 工具 | 适用 | 推荐度 |
|---|---|---|
| **K6** ⭐ | 现代化 / 易编写 | 推荐 |
| JMeter | 传统 / 功能全 | 备选 |
| Gatling | 高性能 / Scala | 备选 |

## K6 示例

```javascript
import http from 'k6/http'
import { check, sleep } from 'k6'

export const options = {
  stages: [
    { duration: '1m', target: 100 },  // 1 分钟爬到 100 并发
    { duration: '3m', target: 100 },  // 保持 3 分钟
    { duration: '1m', target: 0 },    // 1 分钟降到 0
  ],
  thresholds: {
    http_req_duration: ['p(99)<500'],  // P99 < 500ms
    http_req_failed: ['rate<0.01'],    // 错误率 < 1%
  },
}

export default function () {
  const res = http.get('http://localhost:8080/api/v1/users/1')
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  })
  sleep(1)
}
```

## 关键指标

| 指标 | 目标 |
|---|---|
| **P50** | < 100ms |
| **P95** | < 300ms |
| **P99** | < 500ms |
| **错误率** | < 0.1% |
| **QPS** | 按业务需求 |

## 关键约束

- ✅ **MUST** 用 `stages` 渐进加压
- ✅ **MUST** 设阈值（thresholds）
- ✅ **MUST** 压测前确认环境（不压生产）
- ❌ **MUST NOT** 直接压生产（除非明确）

## 关联

- 前置：`integration-testing`
- Wiki：`wiki/_common/performance.md`
- 相关：`performance-tuning`
