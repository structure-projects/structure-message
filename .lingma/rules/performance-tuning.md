---
description: |
triggers:
  - 性能调优
  - 性能优化
  - 接口太慢
  - 系统太慢
  - 响应太慢
  - performance tuning
  - 优化性能
role: devops
priority: medium
category: support
stack: _common
alwaysApply: false
---


# 性能调优

> 系统性诊断与优化性能问题。**先测量，后优化**。

## 调优流程

### 第 1 步：测量

```bash
# 接口延迟
curl -w "@curl-format.txt" -o /dev/null -s http://localhost:8080/api/v1/users/1

# JVM 状态
jstat -gc <pid> 1000

# 线程
jstack <pid>

# 堆 dump
jmap -dump:live,format=b,file=heap.hprof <pid>
```

### 第 2 步：定位瓶颈

按层次排查：
1. **网络**：延迟 / 带宽
2. **应用**：慢方法 / N+1 / 锁竞争
3. **数据**：慢查询 / 索引缺失
4. **缓存**：命中率低 / 穿透
5. **JVM**：GC 频繁 / 内存不足

### 第 3 步：优化

按瓶颈优化：
- **N+1** → 改 JOIN 查询
- **慢查询** → 加索引 / 重写 SQL
- **缓存** → 加缓存 / 调整 TTL
- **JVM** → 调堆 / 换 GC

### 第 4 步：验证

```bash
# 优化后再次测量
# 对比前后指标
```

## 常见优化手段

| 问题 | 优化 |
|---|---|
| N+1 查询 | JOIN / 批量查询 |
| 慢查询 | 索引 / 重写 SQL |
| 缓存穿透 | 缓存空值 / 布隆过滤 |
| 线程池耗尽 | 调整大小 / 拆分池 |
| GC 频繁 | 调堆 / 换 G1 / ZGC |
| 大对象 | 分页 / 流式处理 |

## 关联

- Wiki：`wiki/_common/performance.md`
- 相关：`debug-issue` / `performance-testing`
