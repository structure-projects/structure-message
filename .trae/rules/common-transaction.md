---
alwaysApply: false
globs: "**/*.java, changes/**/*.md"
description: |
---


# 事务规范

> 完整规范详见 `wiki/_common/transaction.md` + `wiki/_common/distributed-transaction.md`

## 硬约束（MUST）

- ✅ **MUST** 事务边界最小化（只把需要的放事务内）
- ✅ **MUST** 分布式事务用 Seata（默认 AT 模式）
- ✅ **MUST** 资金 / 库存用 TCC
- ✅ **MUST** 长流程用 Saga + 状态机
- ✅ **MUST** 所有事务模式幂等

## 禁止（MUST NOT）

- ❌ 在事务内做远程调用（Feign / HTTP）
- ❌ 在事务内做长时间操作
- ❌ 在事务内发消息（用 `@TransactionalEventListener`）
- ❌ 长事务（> 5s）
- ❌ 自调用（事务失效）
- ❌ 非 public 方法用 `@Transactional`（失效）
- ❌ 跨服务用本地 `@Transactional`（无效）

## 关联

- Wiki：`wiki/_common/transaction.md` `wiki/_common/distributed-transaction.md`
- 技能：`coding` / `debug-issue`
