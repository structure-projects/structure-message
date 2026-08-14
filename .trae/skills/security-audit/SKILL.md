---
name: security-audit
description: |
  当用户要求"安全审计/安全扫描/安全检查"时触发。
  系统性检查代码 / 配置 / 依赖的安全问题。

triggers:
  - 安全审计
  - 安全扫描
  - 安全检查
  - security audit
  - 安全评估

role: reviewer
phase: review

when-to-use: |
  需要全面安全审计（如上线前 / 定期检查）。
when-not-to-use: |
  - 仅做代码评审（用 expert-review）
  - 仅修复已知漏洞

allowed-tools: Bash, Read, Grep, Glob

related-rules:
  - common-security

reads-before-action:
  - wiki/_common/security.md

produces:
  - 安全审计报告
  - 漏洞清单 + 修复建议

requires: []

trust-level: standard

require-confirm:
  - 生产环境扫描 MUST 用户确认

mode: auto

category: review
stack: _common
priority: medium
version: "0.3.0"
since: "2026-08-13"
---

# 安全审计

> 系统性安全检查。**生产扫描 MUST 用户确认**。

## 审计维度

### 1. 依赖漏洞

```bash
# Java
mvn dependency-check:check

# Node
npm audit

# 修复
npm audit fix
```

### 2. 代码扫描

```bash
# SonarQube
mvn sonar:sonar

# Semgrep
semgrep --config=auto .
```

### 3. 配置检查

```bash
# 检查 Secrets 是否硬编码
grep -r "password\|secret\|token" --include="*.yaml" --include="*.properties" .

# 检查是否暴露敏感端点
grep -r "management.endpoints.web.exposure.include=*" .
```

### 4. 传输安全

- ✅ HTTPS
- ✅ TLS 1.2+
- ❌ 禁用 TLS 1.0 / 1.1

### 5. 认证授权

- ✅ 密码 BCrypt
- ✅ JWT 过期时间
- ✅ 权限注解完整

### 6. 注入防护

- ✅ SQL 参数化
- ✅ XSS 转义
- ✅ CSRF Token

## 产出物

- 安全审计报告
- 漏洞清单（按严重度分级）
- 修复建议

## 关联

- Wiki：`wiki/_common/security.md`
- 相关：`expert-review` / `debug-issue`
