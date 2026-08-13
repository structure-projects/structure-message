---
description: |
triggers:
  - E2E 测试
  - 端到端测试
  - Playwright
  - Cypress
  - E2E
  - e2e test
role: tester
priority: medium
category: testing
stack: _common
alwaysApply: false
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
