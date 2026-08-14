---
name: k8s-verification
description: |
  当用户要求"验证 K8s 部署/检查 K8s 状态/K8s 健康检查"时触发。
  验证 K8s 部署的健康状态：Pod / Service / Ingress / HPA / 日志。

triggers:
  - 验证 K8s
  - K8s 状态
  - K8s 健康检查
  - 检查部署
  - k8s verification
  - 验证部署

role: devops
phase: deployment

when-to-use: |
  K8s 部署后需要验证健康状态。
when-not-to-use: |
  - 仅部署（用 k8s-deployment）
  - 仅本地测试

allowed-tools: Bash, Read

related-rules:
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/kubernetes.md

produces:
  - 部署验证报告

requires:
  - skill: k8s-deployment
    condition: 部署已执行
    error: 未部署，MUST 先调用 k8s-deployment

trust-level: standard

require-confirm:
  - 生产环境验证 MUST 用户确认

mode: auto

category: deployment
stack: _common
priority: high
version: "0.3.0"
since: "2026-08-13"
---

# K8s 部署验证

> 验证 K8s 部署的健康状态。**MUST 全部通过才算部署成功**。

## 前置条件

- 部署已执行（`k8s-deployment` 完成）

## 执行步骤

### 第 1 步：验证 Deployment

```bash
# 查看 Deployment 状态
kubectl get deployment -n <ns>

# 查看详情
kubectl describe deployment <name> -n <ns>

# 关键字段
# - READY: X/X（所有副本就绪）
# - UP-TO-DATE: X
# - AVAILABLE: X
```

**通过标准**：`READY` = `UP-TO-DATE` = `AVAILABLE` = 期望副本数

### 第 2 步：验证 Pod

```bash
# 查看 Pod 状态
kubectl get pods -n <ns>

# 查看 Pod 详情
kubectl describe pod <pod> -n <ns>

# 查看 Pod 日志
kubectl logs <pod> -n <ns>
kubectl logs <pod> -n <ns> --previous  # 上次崩溃前日志
```

**通过标准**：
- 所有 Pod `STATUS: Running`
- `RESTARTS: 0`（或很少）
- 日志无 ERROR

### 第 3 步：验证 Service

```bash
# 查看 Service
kubectl get svc -n <ns>

# 查看 Endpoints（关键：确认后端 Pod 已加入）
kubectl get endpoints -n <ns>

# 测试 Service 连通性
kubectl port-forward svc/<svc> -n <ns> 8080:80
curl http://localhost:8080/health
```

**通过标准**：Endpoints 含所有 Pod IP

### 第 4 步：验证 Ingress

```bash
# 查看 Ingress
kubectl get ingress -n <ns>

# 查看 Ingress 详情
kubectl describe ingress <name> -n <ns>
```

**通过标准**：Ingress 有 ADDRESS（外部 IP / 域名）

### 第 5 步：验证 HPA（如启用）

```bash
# 查看 HPA
kubectl get hpa -n <ns>

# 关键字段
# - TARGETS: 当前使用率 / 目标使用率
# - MINPODS / MAXPODS
# - REPLICAS: 当前副本数
```

### 第 6 步：健康检查（应用层）

```bash
# 后端健康检查
kubectl port-forward svc/<svc> -n <ns> 8080:80
curl http://localhost:8080/actuator/health

# 预期响应
# {"status":"UP"}
```

### 第 7 步：写入验证报告

写入 `changes/proposals/<current>/deployment.md`：

```markdown
## K8s 部署验证

- [ ] Deployment 就绪
- [ ] Pod 全部 Running
- [ ] Service Endpoints 正常
- [ ] Ingress 已分配地址
- [ ] 健康检查通过
- [ ] 日志无 ERROR

**结论**：✅ 部署成功 / ❌ 部署失败（原因）
```

## 关键约束

- ✅ **MUST** 所有 6 步全部通过
- ❌ **MUST NOT** 任何一项失败就判定成功

## 失败处理

| 失败现象 | 排查 |
|---|---|
| Pod Pending | `kubectl describe pod` 看 Events（资源不足 / 镜像拉取失败） |
| Pod CrashLoopBackOff | `kubectl logs --previous` 看上次崩溃日志 |
| Service 无 Endpoints | 检查 Pod 是否 Running + label 匹配 |
| Ingress 无 ADDRESS | 检查 Ingress Controller 是否运行 |

## 关联

- 前置：`k8s-deployment`
- Wiki：`wiki/_common/kubernetes.md`
- 相关：`kubectl-ops` / `helm-ops`
