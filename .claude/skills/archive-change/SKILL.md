---
name: archive-change
description: |
  当变更完成（编码+测试+评审+部署）后触发。
  MUST 归档提案到 changes/archive/，更新 changelog，检查并更新 README。
  这是 SDLC 的最后一环，禁止跳过。

triggers:
  - 归档
  - 完成变更
  - 结束变更
  - archive
  - 收尾
  - 完成 proposal
  - 关闭变更

role: devops
phase: deployment

when-to-use: |
  变更已完成编码、测试、评审、部署，需要归档。
when-not-to-use: |
  - 变更未完成（仍有未勾选任务）
  - 变更被废弃（用 discard-change 流程）

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - common-git
  - common-version-management
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/version-management.md
  - wiki/_common/documentation.md

produces:
  - changes/archive/<id>/（从 proposals/ 迁移过来）
  - 更新 changes/changelog/<version>.md
  - 更新 README.md（如有 API/功能变化）

requires:
  - skill: deployment-verification
    condition: 部署验证通过（或本地变更无需部署）
    error: 部署未验证，MUST 先完成 deployment-verification

human-in-the-loop:
  - 归档前 MUST 确认所有任务完成
  - changelog 内容 MUST 用户确认

on-failure: |
  tasks.md 未全部勾选 → 禁止归档，提示用户完成剩余任务
  review.md 有未解决 MUST fix → 禁止归档，提示用户先修复

mode: auto

category: deployment
stack: _common
priority: high
version: "0.3.0"
since: "2026-08-13"
---

# 变更归档

> SDLC 的最后一环：归档变更提案，更新 changelog，更新 README。
> **禁止跳过归档** —— 未归档的变更无法追溯。

## 前置条件（MUST 全部满足）

1. **tasks.md 全部勾选**：`changes/proposals/<current>/tasks.md` 无 `- [ ]` 未完成项
2. **review.md 无未解决 MUST fix**
3. **部署验证通过**（或本地变更无需部署）
4. **当前分支为 feat-* / fix-* / hotfix-***

任一不满足 → 禁止归档。

## 执行步骤

### 第 1 步：最终检查

```bash
# 检查 tasks.md
grep -c "^- \[ \]" changes/proposals/<current>/tasks.md
# 预期：0

# 检查 review.md 是否有 MUST fix
grep -A 10 "MUST fix" changes/proposals/<current>/review.md
# 预期：无未勾选
```

### 第 2 步：更新 changelog

写入 `changes/changelog/<version>.md`（如 `1.2.0.md`）：

```markdown
## [1.2.0] - 2026-08-15

### Added
- 新增用户登录接口（proposal: 2026-08-15-add-user-login）

### Changed
- ...

### Fixed
- ...
```

条目 MUST 包含：
- 类型（Added / Changed / Fixed / Security / Deprecated / Removed）
- 简短描述
- 关联 proposal ID

### 第 3 步：更新 README（如有需要）

检查是否需要更新 README：
- 新增 API → 更新 API 列表
- 新增功能 → 更新功能列表
- 修改启动方式 → 更新快速开始
- 依赖变更 → 更新技术栈

**MUST 更新 README 的情况**：
- 影响用户使用方式的变更
- 新增模块 / 新增加载项
- 版本号变化

### 第 4 步：归档提案

```bash
git mv changes/proposals/<id>/ changes/archive/<id>/
```

### 第 5 步：提交归档

```bash
git add changes/ README.md
git commit -m "docs(changes): 归档变更 <id>，更新 changelog <version>"
```

### 第 6 步：（可选）合并到 develop / master

```bash
# 功能分支合并到 develop
git checkout develop
git merge --no-ff feat-<name>

# hotfix 合并到 master + develop
git checkout master
git merge --no-ff hotfix-<version>
git checkout develop
git merge --no-ff hotfix-<version>
```

## 产出物

- `changes/archive/<id>/`（完整提案目录）
- 更新 `changes/changelog/<version>.md`
- 更新 `README.md`（如需要）
- 合并 commit

## 完成标准

- 提案目录已从 proposals/ 移到 archive/
- changelog 含本次变更条目
- README 已更新（如需要）
- 归档 commit 已提交

## 下一步（可选）

归档完成后，可选继续：

- **打 Tag + 发 Release** → 调用 `gh-release` 技能
- **触发发布流水线** → 用 `gh workflow run` 触发对应 workflow（Maven / npm / Docker）
- **结束本次变更** → 无后续

## 关联

- 前置：`deployment-verification`
- 后续（可选）：`gh-release`
- Wiki：`wiki/_common/version-management.md` `wiki/_common/documentation.md`
