---
name: scaffold-project
description: |
  当用户要求"初始化项目/新建项目/搭建项目/scaffold/创建工程"时触发。
  MUST 询问用户项目形态（DDD 7+1 / 单体多模块 / 单体单模块），禁止默认。
  按 stack-constraints 和 project-scaffolding wiki 初始化项目结构。

triggers:
  - 初始化项目
  - 新建项目
  - 搭建项目
  - 创建工程
  - scaffold
  - init project
  - 项目初始化
  - 新项目
  - 脚手架

role: architect
phase: requirement

when-to-use: |
  用户要求从零开始创建新项目、初始化项目结构。
when-not-to-use: |
  - 在已有项目里加新功能（用 requirement-analysis + coding）
  - 仅修改项目配置

allowed-tools: Bash, Read, Write, Edit, Glob, Grep

related-rules:
  - common-naming
  - common-project-structure
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/project-structure.md
  - wiki/_common/architecture.md
  - wiki/<stack>/project-scaffolding.md
  - wiki/<stack>/components.md

stack-constraints:
  structure-boot:
    spring-boot-version: "4.0.6"
    jdk: "17+"
    parent: "cn.structured:structure-dependencies:1.4.4"
    required-components:
      - structure-security
      - structure-infra
      - structure-restful-web-starter
    project-form:
      default: "DDD 7+1 多模块"
      alternatives: ["单体 4 模块 + Manager 模式"]
      must-ask-user: true
  vue3:
    required-components:
      - "@structure-projects/components"
      - "@structure-projects/wujie-subapp"
    project-form:
      default: "wujie 微前端子应用"
      alternatives: ["独立前端项目"]
      must-ask-user: true

produces:
  - 完整项目目录结构
  - pom.xml / package.json（含正确 parent / 依赖）
  - README.md（含项目说明 + 技术栈 + 启动方式）
  - changes/proposals/0001-init-project/（初始化提案）
  - .gitignore / .editorconfig 等基础配置

requires: []

human-in-the-loop:
  - 项目形态（DDD 7+1 / 单体多模块 / 单体单模块）MUST 询问用户
  - 技术栈版本 MUST 与用户确认（默认按 stack-constraints）
  - 项目名 / groupId / packageName MUST 询问用户

on-failure: |
  用户未明确形态 → MUST 追问，MUST NOT 默认
  依赖版本冲突 → 列出冲突项，让用户选择

mode: assist  # 项目初始化涉及大量用户决策，默认 assist 模式

category: requirement
stack: _common
priority: high
version: "0.3.0"
since: "2026-08-13"
---

# 项目初始化

> 从零搭建符合规范的项目结构。**项目形态 MUST 询问用户，禁止默认**。

## 前置条件

- 用户明确要新建项目
- 已识别项目栈（参考 `common-project-stack-detection` 规则）

## 执行步骤

### 第 1 步：询问用户关键决策（MUST）

**禁止默认**，必须按以下决策树引导用户：

#### 1.1 项目形态决策树 ⭐

```
Q1: 项目涉及几个业务领域（聚合根）？
    例如：只有"用户" → 1 个；有"用户+订单+商品" → 多个
    用户回答：___
    ↓
    ├─ 仅 1 个聚合根 + 简单 CRUD → **单体单模块**（工具类项目）
    ↓
    多个聚合根 或 复杂业务 → 继续 Q2

Q2: 团队规模和预期演进周期？
    例如：1 人 / 3 个月 demo / 3 人团队 / 1 年+
    用户回答：___
    ↓
    ├─ 1 人 + 短期（< 3 个月） → **单体单模块**
    ├─ 1-3 人 + 中期（3-12 个月） → **单体 4 模块**
    ↓
    ≥ 3 人 或 长期（≥ 1 年） → 继续 Q3

Q3: 是否需要严格的层间隔离（应用层不接触 Mapper）？
    用户回答：___
    ↓
    ├─ 否 → **单体 4 模块**（用 Manager 模式）
    ↓
    是 → 继续 Q4

Q4: 是否预期拆分为微服务？
    用户回答：___
    ↓
    ├─ 否 → **单体 4 模块**
    ↓
    是 → **DDD 7+1 多模块** ⭐
```

**MUST 告诉用户推荐结果的理由**：
> "根据您的回答，我推荐 **DDD 7+1 多模块**，因为：
>  - 涉及多个业务领域
>  - 团队 ≥ 3 人
>  - 需要层间隔离
>  - 预期拆分为微服务
>
>  详细判断标准见 `wiki/_common/project-form-decision.md`。
>  请确认或选择其他形态。"

#### 1.2 项目信息

- 项目名（如 structure-user）
- groupId（如 cn.structured）
- 主包名（如 cn.structured.user）

#### 1.3 技术栈版本

MUST 按 stack-constraints 确认：
- Spring Boot: 4.0.6
- JDK: 17+
- ...

### 第 2 步：读栈级脚手架 Wiki

```bash
cat wiki/<stack>/project-scaffolding.md
cat wiki/<stack>/components.md
cat wiki/_common/project-form-decision.md  # ⭐ 新增：形态决策详细参考
```

### 第 3 步：生成项目结构

#### DDD 7+1 多模块（默认推荐）

```
structure-{X}/
├── structure-{X}-dependencies/        # 父 POM
├── structure-{X}-common/              # DTO / VO / Query / enums / exception
├── structure-{X}-domain/              # {X}Entity、{X}Repository（接口）、DomainService
├── structure-{X}-infra/               # {X}RepositoryImpl、{X}RepositoryDelegate
├── structure-{X}-repository-mybatis/  # {X}PO、{X}Mapper、{X}MybatisPlusDelegate
├── structure-{X}-application/         # I{X}Service、{X}ServiceImpl、{X}Assembler
├── structure-{X}-interfaces/          # controller/api/ + controller/open/
└── structure-{X}-boot/                # 启动类 + application.yaml
```

#### 单体 4 模块（备选）

```
structure-{X}/
├── {X}-api/           # 接口定义
├── {X}-biz/           # 业务实现
├── {X}-common/        # 通用类
└── {X}-dependencies/  # 父 POM
```

### 第 4 步：生成关键文件

- 根 `pom.xml` 或 `dependencies/pom.xml`：parent = `cn.structured:structure-dependencies:1.4.4`
- 各模块 `pom.xml`
- 启动类（含必要注解）
- `application.yaml`（含栈级必选配置）
- `.gitignore`
- `README.md`（项目说明 + 技术栈 + 启动方式 + 模块结构）

### 第 5 步：初始化 Changes 目录

```bash
mkdir -p changes/proposals/0001-init-project
cp changes/templates/proposal-full.md changes/proposals/0001-init-project/proposal.md
# 填充本次初始化的 proposal 内容
```

### 第 6 步：生成 README.md

README MUST 包含：
- 项目简介
- 技术栈（含版本号，如 Spring Boot 4.0.6 + JDK 17）
- 模块结构图
- 快速开始（如何跑起来）
- 必选组件清单（structure-security / structure-infra 等）
- 开发规范链接（指向 wiki/）

### 第 7 步：初始化 git

```bash
git init
git add .
git commit -m "feat(init): 初始化项目结构（DDD 7+1 多模块）"
```

## 产出物

- 完整项目目录结构
- 正确的 pom.xml 依赖（parent + 必选组件）
- README.md
- changes/proposals/0001-init-project/

## 完成标准

- 项目形态经用户确认
- 所有目录就位
- 所有 pom.xml 编译通过（`mvn clean compile`）
- README 完整
- 首次提交完成

## 关联

- 前置：无
- 后续：`requirement-analysis`（开始第一个需求）
- Wiki：`wiki/<stack>/project-scaffolding.md`
- 规则：`common-project-structure` `common-project-stack-detection`
