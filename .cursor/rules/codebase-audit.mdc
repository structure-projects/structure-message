---
description: |
triggers:
  - 扫描现状
  - 代码审计
  - 项目评估
  - 现状分析
  - audit
  - 代码扫描
role: architect
priority: high
category: requirement
stack: _common
alwaysApply: false
---


# 代码审计

> 老项目接入的第一步：现状扫描，产出 audit-report。

## 执行步骤

### 第 1 步：扫描项目结构

```bash
# 项目类型
ls pom.xml package.json go.mod Cargo.toml 2>/dev/null

# 目录结构（顶层 2 层）
tree -L 2 -d

# 代码规模
find . -name "*.java" | wc -l
find . -name "*.ts" -o -name "*.vue" | wc -l

# Git 历史
git log --oneline | wc -l
git log --since="6 months ago" | wc -l
```

### 第 2 步：规范符合性检查

按维度检查：

| 维度 | 检查项 | 工具 |
|---|---|---|
| **命名** | 类名 / 方法名 / 常量 / 包名 | grep |
| **分支策略** | 当前分支 / 分支列表 | `git branch -a` |
| **Commit 规范** | 最近 20 条 commit message | `git log --oneline -20` |
| **架构分层** | 模块划分 / 依赖方向 | 目录结构 |
| **异常处理** | 是否用 CommonException | grep "throw new" |
| **日志规范** | 是否用 slf4j / 是否含敏感信息 | grep "log\." |
| **API 设计** | 是否 RESTful / 统一响应 | grep "@RestController" |
| **安全** | SQL 注入 / 敏感信息 | grep "\${" 等 |

### 第 3 步：测试评估

```bash
# 测试覆盖率
mvn test jacoco:report  # Java
npm test -- --coverage  # Node
pytest --cov            # Python

# 统计测试文件
find . -name "*Test.java" | wc -l
find . -name "*.test.ts" | wc -l
```

### 第 4 步：CI/CD 评估

```bash
ls .github/workflows/ 2>/dev/null
ls .gitlab-ci.yml 2>/dev/null
ls Jenkinsfile 2>/dev/null
```

### 第 5 步：文档评估

- README 完整度
- 架构文档
- API 文档
- 变更日志

### 第 6 步：产出 audit-report.md

按 `changes/templates/audit-report.md` 模板填充：

```markdown
# 现状审计报告：<项目名>

## 项目概览
- 技术栈 / 模块数 / 代码规模 / 提交历史

## 规范符合性评估
| 维度 | 符合度 | 说明 |

## 测试评估
## CI/CD 评估
## 文档评估
## 主要不合规点
## 改造建议
### 优先级 P0 / P1 / P2
## 迁移建议
```

## 产出物

- `changes/proposals/0000-legacy-onboarding/audit-report.md`

## 完成标准

- 所有维度都检查过
- 主要不合规点列出
- 改造建议分优先级

## 下一步

调用 `migration-planning` 制定迁移计划。

## 关联

- 调用方：`legacy-onboarding`
- 后续：`migration-planning`
- Wiki：`wiki/_common/legacy-onboarding.md`
