---
alwaysApply: false
globs: "**/*Test.java, **/*IT.java, **/*.test.ts, **/*.spec.ts, changes/**/*.md"
description: |
---


# 测试规范

> 完整规范详见 `wiki/_common/testing-strategies.md`

## 硬约束（MUST）

- ✅ **MUST** 新代码 MUST 有对应单测
- ✅ **MUST** 单测覆盖率 ≥ 80%，分支覆盖 ≥ 70%
- ✅ **MUST** 测试命名 `should<Expected>When<Condition>`
- ✅ **MUST** 测试结构 Arrange / Act / Assert
- ✅ **MUST** 集成测试用 Testcontainers（真实中间件）
- ✅ **MUST** 测试独立（无顺序依赖）

## 禁止（MUST NOT）

- ❌ 跳过失败测试提交
- ❌ 用 H2 替代 MySQL（行为不一致）
- ❌ 用 `@MockBean` 替代真实中间件（集成测试）
- ❌ 用 `page.waitForTimeout`（E2E 测试）

## 关联

- Wiki：`wiki/_common/testing-strategies.md`
- 技能：`unit-testing` / `integration-testing` / `e2e-testing`
