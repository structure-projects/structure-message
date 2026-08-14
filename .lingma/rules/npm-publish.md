---
description: |
triggers:
  - 发布 npm
  - 发 npm
  - npm publish
  - npm 发布
  - 发布组件库
  - publish npm
role: devops
priority: high
category: deployment
stack: _common
alwaysApply: false
---


# npm 发布

> 按生态规范发布 npm 包。**仅组件库可发布；业务包 MUST private: true**。

## 前置条件

- CI 通过
- 版本号符合语义化版本
- `package.json` 含 `publishConfig.access: "public"` + `files: ["dist"]`

## 关键约束（MUST 遵守）

### package.json 配置

```json
{
  "name": "@structure-projects/components",
  "version": "1.2.0",
  "private": false,
  "publishConfig": {
    "access": "public"
  },
  "files": ["dist"],
  "scripts": {
    "build": "...",
    "prepublishOnly": "npm run build && npm run test"
  }
}
```

### 业务包禁止发布

```json
{
  "name": "user-ui",
  "private": true
}
```

**规则**：
- ✅ **MUST** 组件库用 `@structure-projects` scope
- ✅ **MUST** 组件库 `private: false` + `publishConfig.access: "public"`
- ❌ **MUST NOT** 业务包（`*-ui`）发布到 npm

## 执行步骤

### 第 1 步：发布前检查

```bash
# 检查 package.json
cat package.json | jq '.name, .version, .private, .publishConfig'

# 校验
# - name MUST @structure-projects/*
# - private MUST != true
# - publishConfig.access MUST = "public"
```

### 第 2 步：升级版本

```bash
# 语义化版本
npm version patch  # 1.0.0 → 1.0.1（修复）
npm version minor  # 1.0.0 → 1.1.0（新功能）
npm version major  # 1.0.0 → 2.0.0（破坏性）

# 或指定版本
npm version 1.2.0 --no-git-tag-version
```

### 第 3 步：构建 + 测试

```bash
npm ci
npm run build
npm run test
```

### 第 4 步：发布

**MUST 用户确认后执行**：

```bash
# 登录（如未登录）
npm login

# 发布
npm publish --access public

# 或干跑预览
npm publish --dry-run
```

### 第 5 步：验证

```bash
# 查看包信息
npm view @structure-projects/components

# 安装验证
npm install @structure-projects/components@1.2.0

# 在测试项目里 import 验证
```

### 第 6 步：打 Tag

```bash
git tag -a v1.2.0 -m "Release @structure-projects/components v1.2.0"
git push origin v1.2.0

# （可选）创建 GitHub Release
gh release create v1.2.0 --title "v1.2.0" --generate-notes
```

## 常见问题

### 403 Forbidden

- 原因：npm token 失效 或 无权限
- 修复：`npm login` 或检查 token

### 版本冲突

- 原因：版本号已存在
- 修复：`npm version patch` 升版本号

### 包名错误

- 原因：scope 不对
- 修复：确认 `name` 含 `@structure-projects/` 前缀

## 产出物

- 发布的 npm 包
- 更新 package.json version
- Git Tag
- （可选）GitHub Release

## 关联

- 前置：`ci-gate`
- 相关：`gh-release` / `maven-publish`
- Wiki：`wiki/_common/npm-publish.md` `wiki/_common/version-management.md`
