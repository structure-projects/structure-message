---
name: coding
description: |
  当用户要求"按提案实现/开始编码/写代码/实现"时触发。
  MUST 在 changes/proposals/ 存在当前提案时才能执行。
  按 tasks.md 逐项实现 + 写单测 + 本地验证。

triggers:
  - 按提案
  - 开始编码
  - 写代码
  - 实现
  - 编码
  - 开发
  - code
  - implement
  - 按设计

role: developer
phase: coding

when-to-use: |
  变更提案已存在，用户要求开始实现。
when-not-to-use: |
  - 无变更提案时（MUST 先调用 requirement-analysis）
  - 仅修复 typo 等 trivial 变更

allowed-tools: Bash, Read, Write, Edit, Glob, Grep

related-rules:
  - common-naming
  - common-git
  - common-project-structure
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/naming.md
  - wiki/_common/architecture.md
  # 栈级规范（MUST 根据识别的栈动态替换 <stack>）
  - wiki/<stack>/developer.md
  - wiki/<stack>/components.md

produces:
  - 源代码
  - 单元测试
  - 更新的 changes/proposals/<current>/tasks.md

requires:
  - skill: requirement-analysis
    condition: changes/proposals/<current>/proposal.md exists
    error: 未找到变更提案，MUST 先调用 requirement-analysis 技能

human-in-the-loop:
  - 遇到 proposal 未覆盖的边界 MUST 回到 requirement-analysis 补充 proposal
  - 关键设计决策 MUST 与用户确认

on-failure: |
  编译失败 → 修复后重试；3 次失败 MUST 停下来问用户
  测试失败 → 修复后重试；MUST NOT 跳过失败测试
  proposal 外情况 → 回到 requirement-analysis 补充

mode: auto  # auto | assist（assist 模式下 AI 仅辅助，用户主导编码）

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

category: coding
stack: _common
priority: high
version: "0.3.0"
since: "2026-08-13"
---

# 编码实现

> 按变更提案编码。MUST 按 tasks.md 逐项完成 + 写单测。

## 前置条件（MUST 全部满足）

1. **变更提案存在**：`changes/proposals/<current>/proposal.md` 存在
2. **任务清单存在**：`changes/proposals/<current>/tasks.md` 有未完成任务
3. **分支正确**：当前分支匹配 `^(feat|fix|hotfix)-*`

任一不满足 → MUST 停止并提示：
- 无提案 → 调用 `requirement-analysis` 技能
- 分支错误 → 切到 `feat-*` / `fix-*` 分支

## 执行步骤

### 第 0 步：判断目录类型（如涉及"新建目录/子目录/特性"）⭐

如果用户请求中涉及"新建目录/子目录/特性/模块"，MUST 先判断目录类型：

| 用户表达 | 目录类型 | 行动 |
|---|---|---|
| "新建包 / 子包 / package" | 包目录 | 按 Java 包规范创建（影响 package 语句） |
| "新建特性 / feature / 业务模块" | 特性目录 | 调用 `create-feature` 技能（跨层组织） |
| "新建文档/脚本/示例目录" | 非代码目录 | 创建独立目录（docs/scripts/examples） |
| "新建子目录"（未明确） | **MUST 询问** | 让用户确认类型 |

**禁止**：
- ❌ MUST NOT 把"子目录"默认按"子包"处理
- ❌ MUST NOT 在 `src/main/java/` 下创建非代码目录

详见 `common-project-structure` 规则的"目录类型识别"章节。

### 第 1 步：读变更提案

```bash
cat changes/proposals/<current>/proposal.md
cat changes/proposals/<current>/tasks.md
# 复杂变更：
cat changes/proposals/<current>/design.md
```

### 第 2 步：读相关 Wiki（TODO 阶段 3 填充栈级引用）

MUST Read：
- `wiki/_common/naming.md`
- `wiki/_common/architecture.md`
- `wiki/<stack>/developer.md`

### 第 3 步：按 tasks.md 逐项实现

对每一项未完成任务：
1. 读任务描述
2. 写代码（遵守所有相关 rules 约束）
3. 写对应单测（MUST 与代码同步完成）
4. 本地验证：编译通过 + 相关单测通过
5. 勾选任务：在 tasks.md 中将 `- [ ]` 改为 `- [x]`

**关键约束**：
- MUST 完成一项再做下一项
- MUST 代码 + 单测同步完成
- 遇到 proposal 未覆盖的边界 → MUST 回到 `requirement-analysis` 补充 proposal

### 第 4 步：全部完成后的本地验证（TODO 阶段 3 填充栈级命令）

### 第 5 步：自评

- 代码是否实现 proposal 所有目标？
- 是否触碰了非目标范围？
- 是否有 proposal 未预见的问题？

## 产出物

- 源代码（符合 rules 约束）
- 单元测试（覆盖率 ≥ 80%）
- 更新的 `changes/proposals/<current>/tasks.md`

## 完成标准

- tasks.md 所有任务勾选完成
- 本地编译通过
- 本地测试全部通过
- 代码符合所有 rules 约束

## 下一步

- 并行：调用 `unit-testing` 完善测试 / 调用 `expert-review` 评审代码
- 然后：调用 `ci-gate` 提交代码

## 关联

- 前置：`requirement-analysis`
- 并行：`unit-testing` `expert-review`
- 后续：`ci-gate`
- Wiki：`wiki/<stack>/developer.md`
- 规则：`<stack>-developer` `common-naming`
