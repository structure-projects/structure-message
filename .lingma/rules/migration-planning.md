---
description: |
triggers:
  - 制定迁移计划
  - 规划迁移
  - 迁移策略
  - migration planning
  - 迁移计划
role: architect
priority: high
category: requirement
stack: _common
alwaysApply: false
---


# 迁移规划

> 基于 audit-report 制定迁移计划。**策略 MUST 用户确认**。

## 前置条件

- `audit-report.md` 已完成

## 执行步骤

### 第 1 步：读 audit-report

```bash
cat changes/proposals/0000-legacy-onboarding/audit-report.md
```

### 第 2 步：确定改造范围（MUST 用户确认）

```
Q1: 改造范围？
    a) 全部（一次性迁移）—— 风险高，仅小项目
    b) 部分（仅新代码按新规范）—— 风险低
    c) 渐进（接触到的老代码顺手改）—— 推荐 ⭐
```

### 第 3 步：选择迁移策略（MUST 用户确认）

```
Q2: 迁移策略？
    a) 冻结：老代码不动，仅新代码按新规范
       适用：稳定老项目，不演进
    
    b) 渐进改造（Boy Scout Rule）：接触到的老代码顺手改 ⭐ 推荐
       适用：持续维护的项目
    
    c) Strangler Fig：新功能在新模块，老功能逐步替换
       适用：大型重构，服务拆分
    
    d) 整体重写：一次性重写
       适用：极少推荐（风险极高）
```

### 第 4 步：制定阶段规划

按改造范围拆分阶段：

```
M1：基础规范接入（1 周）
  - 安装 rules / skills / wiki / changes
  - 配置 commit-msg hook
  - 建立 CI 基础

M2：核心模块改造（2 周）
  - 按优先级改造核心模块
  - 补充关键测试

M3：边缘模块改造（按需）
  - 剩余模块
  - 补充文档
```

### 第 5 步：评估风险

| 风险 | 影响 | 缓解 |
|---|---|---|
| 老代码改造引入 bug | 高 | 渐进改造 + 完整测试 |
| 双规范并存期混乱 | 中 | 明确边界（新代码 vs 老代码） |
| 团队学习成本 | 中 | 培训 + 文档 + 示例 |
| 进度延误 | 中 | 阶段拆分 + 每周回顾 |

### 第 6 步：产出迁移提案

写入 `changes/proposals/0000-legacy-onboarding/proposal.md`：

```markdown
# 迁移变更提案：老项目接入

## 现状
<来自 audit-report>

## 目标状态
<接入本规范后的样子>

## 迁移策略
<冻结 / 渐进改造 / Strangler Fig / 整体重写>

## 阶段规划
| 里程碑 | 范围 | 完成标准 |

## 风险评估
## 回滚预案
## 兼容性保证
## 双规范并存期约定
```

## 产出物

- `changes/proposals/0000-legacy-onboarding/proposal.md`
- `changes/proposals/0000-legacy-onboarding/tasks.md`

## 完成标准

- 迁移策略经用户确认
- 阶段规划明确
- 风险评估完整
- 双规范并存期约定清晰

## 下一步

- （可选）调用 `retro-document` 反向生成文档
- 进入正常 SDLC（新需求 → `requirement-analysis`）

## 关联

- 调用方：`legacy-onboarding`
- 前置：`codebase-audit`
- 后续：`retro-document`（可选）/ `requirement-analysis`
- Wiki：`wiki/_common/migration-strategies.md`
