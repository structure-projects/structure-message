---
name: e2e-testing
description: |
  当用户要求"写 E2E 测试/端到端测试/Playwright/Cypress"时触发。
  编写端到端用户场景测试。

triggers:
  - E2E 测试
  - 端到端测试
  - Playwright
  - Cypress
  - E2E
  - e2e test

role: tester
phase: testing

when-to-use: |
  需要端到端用户场景测试（UI + 后端 + DB 全链路）。
when-not-to-use: |
  - 仅单测（用 unit-testing）
  - 仅集成测试（用 integration-testing）

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - common-testing
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/testing-strategies.md

produces:
  - E2E 测试代码（Playwright / Cypress）
  - 测试报告 / 截图 / 视频

requires:
  - skill: integration-testing
    condition: 集成测试已通过

trust-level: standard

mode: auto

category: testing
stack: _common
priority: medium
version: "0.3.0"
since: "2026-08-13"
---

# E2E 测试

> 端到端用户场景测试。**MUST 覆盖核心业务流程**。

## 工具选择

| 工具 | 推荐度 | 说明 |
|---|---|---|
| **Playwright** ⭐ | 推荐 | 多浏览器 / 快 / 内置等待 |
| Cypress | 备选 | 易上手 / 社区大 |
| Selenium | 不推荐 | 老旧 |

## Playwright 示例

```typescript
import { test, expect } from '@playwright/test'

test.describe('用户登录', () => {
  test('正常登录', async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[name="username"]', 'admin')
    await page.fill('input[name="password"]', 'password')
    await page.click('button[type="submit"]')
    await expect(page).toHaveURL('/dashboard')
    await expect(page.locator('.user-name')).toHaveText('admin')
  })

  test('密码错误', async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[name="username"]', 'admin')
    await page.fill('input[name="password"]', 'wrong')
    await page.click('button[type="submit"]')
    await expect(page.locator('.error-message')).toBeVisible()
  })
})
```

## 关键约束

- ✅ **MUST** 覆盖核心业务流程（登录 / 下单 / 支付）
- ✅ **MUST** 用 `data-testid` 选择器（不用 CSS class）
- ✅ **MUST** 测试独立（无顺序依赖）
- ❌ **MUST NOT** 用 `page.waitForTimeout`（应用 `waitForSelector`）

## 关联

- 前置：`integration-testing`
- Wiki：`wiki/_common/testing-strategies.md`
- 相关：`unit-testing`
