---
name: changelog-generation
description: |
  当用户要求"生成 changelog/写变更日志/补 changelog"时触发。
  按 Keep a Changelog 规范生成变更日志。

triggers:
  - 生成 changelog
  - 写变更日志
  - 补 changelog
  - changelog
  - 变更日志

role: developer
phase: support

when-to-use: |
  需要生成或补充 changelog。
when-not-to-use: |
  - 仅归档变更（用 archive-change）

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - common-documentation
  - common-version-management

reads-before-action:
  - wiki/_common/version-management.md
  - wiki/_common/documentation.md

produces:
  - 更新的 changes/changelog/<version>.md

requires: []

trust-level: standard

auto-decisions:
  - changelog-entry-format

mode: auto

category: documentation
stack: _common
priority: medium
version: "0.3.0"
since: "2026-08-13"
---

# Changelog 生成

> 按 Keep a Changelog 规范生成变更日志。

## 格式

```markdown
## [X.Y.Z] - YYYY-MM-DD

### Added
- <新功能>（proposal: <id>）

### Changed
- <变更>（proposal: <id>）

### Deprecated
- <废弃>

### Removed
- <移除>

### Fixed
- <修复>（proposal: <id>）

### Security
- <安全>
```

## 执行步骤

### 第 1 步：读已归档提案

```bash
ls changes/archive/
```

### 第 2 步：按类型分组

- feat → Added
- fix → Fixed
- refactor → Changed
- ...

### 第 3 步：生成 changelog

写入 `changes/changelog/<version>.md`

### 第 4 步：提交

```bash
git add changes/changelog/
git commit -m "docs(changelog): 更新 <version> 变更日志"
```

## 关联

- Wiki：`wiki/_common/version-management.md`
- 相关：`archive-change` / `gh-release`
