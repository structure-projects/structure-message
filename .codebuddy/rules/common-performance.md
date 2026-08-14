---
alwaysApply: false
globs: "**/*.java, **/*.sql, changes/**/*.md"
description: |
---


# 性能优化规范

> 完整规范详见 `wiki/_common/performance.md`

## 硬约束（MUST）

- ✅ **MUST** 先测量，后优化（不优化未测量的代码）
- ✅ **MUST** 高频查询字段有索引
- ✅ **MUST** 大表分页用主键范围（不用 OFFSET）
- ✅ **MUST** 批量操作（不循环单条插入）
- ✅ **MUST** 关键指标有监控（响应时间 / QPS / 错误率 / CPU / 内存）

## 禁止（MUST NOT）

- ❌ N+1 查询
- ❌ `SELECT *`
- ❌ 在 WHERE 里对字段做函数操作
- ❌ 在低选择性字段建索引

## 关联

- Wiki：`wiki/_common/performance.md` `wiki/_common/cache-design.md`
- 技能：`performance-tuning` / `performance-testing` / `debug-issue`
