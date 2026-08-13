---
description: |
triggers:
  - 生成 changelog
  - 写变更日志
  - 补 changelog
  - changelog
  - 变更日志
role: developer
priority: medium
category: documentation
stack: _common
alwaysApply: false
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
