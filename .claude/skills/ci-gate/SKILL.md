---
name: ci-gate
description: |
  当用户要求"提交/commit/推送/push/合并/merge"时触发。
  MUST 执行本地预检 + 物理拦截 + CI 监控。
  紧急 hotfix 走快速 CI 通道（仅 MUST 检查）。

triggers:
  - 提交
  - commit
  - 推送
  - push
  - 合并
  - merge
  - 提交代码
  - 保存
  - 打个点
  - checkpoint

role: devops
phase: ci

when-to-use: |
  编码 + 测试 + 评审完成后，需要提交代码并触发 CI。
when-not-to-use: |
  - 无 staged 变更（提示用户先 git add）
  - 编码未完成（MUST 先完成 coding + unit-testing + expert-review）
  - 仅查询 git 状态

allowed-tools: Bash, Read

related-rules:
  - common-git
  - common-naming
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/git.md
  - wiki/_common/ci-cd-pipeline.md
  # 栈级规范（MUST 根据识别的栈动态替换 <stack>）
  - wiki/<stack>/developer.md
  - wiki/<stack>/components.md

produces:
  - git commit
  - git push
  - CI 通过

requires:
  - skill: coding
    condition: tasks.md all checked
  - skill: unit-testing
    condition: 本地测试通过
  - skill: expert-review
    condition: changes/proposals/<current>/review.md exists
    error: 缺少评审报告，MUST 先调用 expert-review

human-in-the-loop:
  - 推送到 master / develop MUST 用户确认
  - 强制推送（force push）MUST 用户确认

on-failure: |
  本地预检失败 → 修复后重试
  CI 失败 → 分析日志修复；3 次失败 MUST 停下来问用户
  hotfix 紧急 → 走快速 CI（仅 MUST 检查）

mode: auto

# 栈级硬约束（MUST 遵守）
stack-constraints:
  structure-boot:
    spring-boot-version: "4.0.6"
    jdk: "17+"
    parent: "cn.structured:structure-dependencies:1.4.4"
    required-components:
      - structure-security
      - structure-infra
      - structure-restful-web-starter
    forbidden:
      - "Jackson / Gson"
      - "RestTemplate / WebClient"
      - "Spring Boot 3.x"
  vue3:
    required-components:
      - "@structure-projects/components"
      - "@structure-projects/wujie-subapp"
      - "@structure-projects/gateway-client"
    forbidden:
      - "Vue 2"
  react:
    forbidden:
      - "class 组件（必须函数式 + Hooks）"

category: ci
stack: _common
priority: high
version: "0.3.0"
since: "2026-08-13"
---

# CI 门禁

> 提交代码的物理门禁。本地预检 + git hooks + CI 监控。
> 即使绕过其他 skills，本层也拦得住。

## 前置条件（MUST 全部满足）

1. **编码完成**：`tasks.md` 所有任务勾选
2. **测试通过**：本地测试全部通过
3. **评审完成**：`changes/proposals/<current>/review.md` 存在，无未解决的 MUST fix

## 分级检查

### MUST 检查（任何提交都必须通过）

- commit-msg 格式（Conventional Commits）
- 分支名（`feat-*` / `fix-*` / `hotfix-*` / `release-*`）
- 编译通过（`mvn clean package -DskipTests` / `npm run build` / ...）
- 核心单测通过

### SHOULD 检查（hotfix 可降级）

- 覆盖率 ≥ 80%
- 全量测试通过
- lint 无 error
- 安全扫描通过

## 执行步骤

### 第 1 步：本地预检

```bash
# 分支检查
git branch --show-current | grep -E "^(feat|fix|hotfix|release)-"

# 编译
mvn clean package -DskipTests  # 或 npm run build / pytest

# 核心单测
mvn test  # 或 npm test / pytest
```

### 第 2 步：生成 commit message

调用 `git-commit` 子技能：
- 按 Conventional Commits 生成 `<type>(<scope>): <description>`
- 校验 commit-msg hook

### 第 3 步：提交

```bash
git commit -m "<message>"
```

### 第 4 步：推送

```bash
git push origin <branch>
# 首次推送：
git push -u origin <branch>
```

### 第 5 步：监控远程 CI

- 追踪 CI 状态（GitHub Actions / GitLab CI / Jenkins）
- 失败 MUST 修复，不允许"先合并再说"

## Hotfix 快速通道

紧急 hotfix 时可降级 SHOULD 检查：
- 跳过覆盖率检查
- 跳过全量测试（仅跑核心单测）
- 事后 24h 内补跑完整 CI

MUST 检查任何情况都不可跳过。

## 完成标准

- commit-msg hook 通过
- 编译通过
- 核心单测通过
- 远程 CI 通过
- 推送成功

## 关联

- 前置：`coding` `unit-testing` `expert-review`
- 后续：`deployment-verification`
- 子技能：`git-commit`
- Wiki：`wiki/_common/git.md` `wiki/_common/ci-cd-pipeline.md`
- 物理拦截：`_common/checks/commit-msg.sh`
