---
name: requirement-analysis
description: |
  当用户提出"新需求/新功能/新增/做个/实现/想要/feature"时触发。
  MUST 在任何编码动作前调用本技能产出变更提案。
  未产出 proposal 时禁止进入编码阶段。

triggers:
  - 需求
  - 新需求
  - 功能
  - 新功能
  - 新增
  - 做个
  - 实现
  - 想要
  - feature
  - requirement
  - new feature

role: architect
phase: requirement

when-to-use: |
  用户提出任何新需求、新功能、变更请求。
  包括显式（"帮我做个登录功能"）和隐式（"我们需要支持导出 Excel"）。

when-not-to-use: |
  - 用户只是问问题（如何、为什么），没要求实施
  - trivial 级别变更（typo、文档改进、注释修正）—— 直接改 + changelog
  - hotfix 紧急修复 —— 走极简 proposal（见 proposal-hotfix.md）

allowed-tools: Bash, Read, Write, Edit, Glob, Grep

related-rules:
  - common-naming
  - common-git
  - common-project-structure
  - common-project-stack-detection

reads-before-action:
  # 通用规范
  - wiki/_common/architecture.md
  - wiki/_common/project-structure.md
  - wiki/_common/git.md
  # 栈级规范（MUST 根据识别的栈动态替换 <stack>）
  - wiki/<stack>/architect.md
  - wiki/<stack>/project-scaffolding.md
  - wiki/<stack>/components.md

# 栈级硬约束（MUST 遵守）
# 安装器会把这些约束注入到 router.mdc，AI 可看到当前项目的必选/禁用项
stack-constraints:
  structure-boot:
    spring-boot-version: "4.0.6"
    jdk: "17+"
    parent: "cn.structured:structure-dependencies:1.4.4"
    required-components:
      - structure-security        # 安全框架（含 JWT）
      - structure-infra           # Repository 基础设施
      - structure-restful-web-starter  # JSON 序列化（FastJSON）
    forbidden:
      - "Jackson / Gson"          # 禁止混用 JSON 库
      - "RestTemplate / WebClient" # 禁止非 Feign 调用
      - "Spring Boot 3.x"         # 禁止旧版本
    project-form:
      default: "DDD 7+1 多模块"
      alternatives: ["单体 4 模块 + Manager 模式"]
      must-ask-user: true          # 禁止默认，必须询问
  vue3:
    required-components:
      - "@structure-projects/components"
      - "@structure-projects/wujie-subapp"
      - "@structure-projects/gateway-client"
    forbidden:
      - "Vue 2"
      - "全局注册 components（必须按需命名导入）"
    project-form:
      default: "wujie 微前端子应用"
      alternatives: ["独立前端项目"]
      must-ask-user: true
  react:
    required-components:
      - "@structure-projects/components-react"  # 如适用
    forbidden:
      - "class 组件（必须函数式 + Hooks）"
    project-form:
      default: "函数式 + Hooks"
      must-ask-user: false

produces:
  - changes/proposals/<id>/proposal.md
  - changes/proposals/<id>/tasks.md
  - changes/proposals/<id>/design.md (可选，复杂需求必填)

requires: []

human-in-the-loop:
  - 需求澄清 MUST 与用户确认
  - proposal 完成后 MUST 用户确认才能进入编码
  - 项目形态（DDD 多模块 / 单体）MUST 询问用户，禁止默认

on-failure: |
  需求不清晰 → MUST 追问，MUST NOT 假设
  发现 proposal 未覆盖的边界 → 回到本技能补充 proposal
  栈识别失败 → MUST 问用户，禁止默认

mode: auto

category: requirement
stack: _common
priority: high
version: "0.3.0"
since: "2026-08-13"
---

# 需求分析

> 本技能是 SDLC 的入口。任何编码动作 MUST 先经本技能产出变更提案。

## 双流程区分（MUST 先判断项目类型）⭐

### 新项目流程

```
需求分析（本技能）
   ↓
概要设计（high-level-design）
   ↓
详细设计（detailed-design）
   ↓
编码（coding）
```

**适用**：从零开始的新项目 / 大版本重构 / 架构演进

**MUST 完成 HLD + LLD 才能进入编码**。

### 历史项目流程

```
需求分析（本技能）
   ↓
（可选）详细设计（detailed-design，仅 major 变更）
   ↓
编码（coding）
```

**适用**：已有项目的功能更新 / 简单修复

**判断标准**：
- major 变更（新功能 / 架构调整）→ 走详细设计
- minor / trivial / hotfix → 跳过详细设计，直接 coding

### 如何判断是新项目还是历史项目

```bash
# 检查项目是否有源代码
ls src/ 2>/dev/null
ls */src/ 2>/dev/null
ls pom.xml package.json go.mod 2>/dev/null
```

- 无源代码或仅脚手架 → **新项目**
- 有完整源代码 + git 历史 → **历史项目**

不确定时 MUST 问用户。

## 前置条件（MUST 全部通过）

- `changes/` 目录已初始化（由安装器完成）
- 无（本技能是 SDLC 起点）

## 变更级别识别（MUST 先判断）

| 级别 | 触发场景 | 模板 |
|---|---|---|
| **trivial** | typo、文档、格式、注释 | 仅 changelog，跳过本技能 |
| **minor** | 小功能调整、简单 bug | `templates/proposal-simple.md` |
| **major** | 新功能、架构调整 | `templates/proposal-full.md` + `design.md` |
| **hotfix** | 生产紧急修复 | `templates/proposal-hotfix.md`（走快速通道） |
| **migration** | 老项目改造 | `templates/proposal-migration.md` |

不确定级别时 MUST 询问用户。

## 执行步骤

### 第 1 步：澄清需求（TODO 阶段 3 填充细节）

### 第 2 步：影响分析（TODO 阶段 3 填充细节）

### 第 3 步：技术方案（TODO 阶段 3 填充细节）

### 第 4 步：生成提案 ID

格式：`YYYYMMDD-<kebab-case-name>`，示例：`2026-08-15-add-user-login`

### 第 5 步：产出变更提案目录

```bash
mkdir -p changes/proposals/<id>
cp changes/templates/proposal-<level>.md changes/proposals/<id>/proposal.md
cp changes/templates/tasks.md changes/proposals/<id>/
# 复杂需求：
cp changes/templates/design.md changes/proposals/<id>/
```

### 第 6 步：创建分支

```bash
git checkout develop && git pull
git checkout -b feat-<name>  # 或 fix-<name> / hotfix-<name>
```

### 第 7 步：提交变更提案

```bash
git add changes/proposals/<id>/
git commit -m "docs(changes): 新增变更提案 <id>"
```

## 产出物

- `changes/proposals/<id>/proposal.md`
- `changes/proposals/<id>/tasks.md`
- `changes/proposals/<id>/design.md`（可选）
- 新分支 `feat-<name>` 或 `fix-<name>`

## 完成标准

- proposal.md 所有字段填写完整
- tasks.md 任务清单 ≥ 3 项
- 分支创建成功
- 变更提案已提交
- 用户已确认 proposal

## 下一步

按项目类型选择：

- **新项目** → 调用 `high-level-design` 技能（概要设计）
- **历史项目 major 变更** → 调用 `detailed-design` 技能（详细设计）
- **历史项目 minor / trivial / hotfix** → 直接调用 `coding` 技能

## 关联

- Wiki：`wiki/_common/architecture.md` `wiki/_common/project-structure.md` `wiki/_common/high-level-design.md` `wiki/_common/detailed-design.md`
- 规则：`common-naming` `common-git`
- 模板：`changes/templates/proposal-*.md` `changes/templates/tasks.md` `changes/templates/design.md`
