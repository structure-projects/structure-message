---
description: |
triggers:
  - 打 tag
  - 发 Release
  - 发布版本
  - GitHub Release
  - gh release
  - 发版
  - 创建 release
role: devops
priority: high
category: deployment
stack: _common
alwaysApply: false
---


# GitHub Release

> 创建 GitHub Release + Tag。**SDLC 的最后一步**。

## 前置条件（MUST 全部满足）

1. **变更已归档**：`changes/archive/<id>/` 存在
2. **changelog 已更新**：`changes/changelog/<version>.md` 含本次条目
3. **当前分支为 master**（或 develop，视分支策略）
4. **本地与远程同步**：`git pull` 最新

## 执行步骤

### 第 1 步：确认版本号

```bash
# 读取当前版本
grep -m1 "version" pom.xml  # Java
# 或
jq -r .version package.json  # Node

# 确认目标版本（MUST 用户确认）
# 例如：1.2.0
```

### 第 2 步：打 Tag

```bash
# 切到 master 并拉最新
git checkout master
git pull

# 打附注 Tag
git tag -a v1.2.0 -m "Release v1.2.0"

# 推送 Tag
git push origin v1.2.0
```

**关键约束**：
- ✅ **MUST** 用附注 Tag（`git tag -a`）
- ✅ **MUST** Tag 格式 `v<X.Y.Z>`（如 `v1.2.0`）
- ❌ **MUST NOT** 用轻量 Tag（`git tag`，无附注）

### 第 3 步：创建 GitHub Release

```bash
# 方式 A：从 changelog 生成 notes（推荐）
gh release create v1.2.0 \
  --title "v1.2.0" \
  --notes-file changes/changelog/1.2.0.md

# 方式 B：自动生成 notes
gh release create v1.2.0 --generate-notes

# 方式 C：手动编写 notes
gh release create v1.2.0 \
  --title "v1.2.0" \
  --notes "$(cat <<'EOF'
## Added
- 新增用户登录接口

## Fixed
- 修复 token 过期问题
EOF
)"
```

**MUST 用户确认后执行**。

### 第 4 步：（可选）触发发布流水线

根据项目类型触发对应的发布流水线：

```bash
# Maven 项目
gh workflow run release-maven.yml \
  -f module=<module> \
  -f version=1.2.0

# npm 组件
gh workflow run publish-npm.yml \
  -f component=<component> \
  -f version=1.2.0

# Docker 镜像
gh workflow run build-and-push.yml \
  -f module=<module> \
  -f version=1.2.0
```

**MUST 用户确认后执行**。

### 第 5 步：验证

```bash
# 查看 Release
gh release view v1.2.0

# 查看 workflow 运行
gh run list --workflow=release-maven.yml
```

## 产出物

- Git Tag（`v<X.Y.Z>`）
- GitHub Release
- （可选）触发发布流水线

## 完成标准

- Tag 推送成功
- Release 创建成功
- 发布流水线触发（如需要）

## 关键约束

- ✅ **MUST** Tag 用附注（`git tag -a`）
- ✅ **MUST** Release notes 从 changelog 生成
- ✅ **MUST** 用户确认后执行
- ❌ **MUST NOT** 在 develop 上打 Release Tag
- ❌ **MUST NOT** 跳过 archive-change 直接 Release

## 关联

- 前置：`archive-change`
- 后续：（可选）`ci-pipeline-design` 配置的发布流水线
- Wiki：`wiki/_common/version-management.md` `wiki/_common/github-workflow.md`
