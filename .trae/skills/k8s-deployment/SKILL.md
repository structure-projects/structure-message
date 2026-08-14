---
name: k8s-deployment
description: |
  当用户要求"写 K8s 部署/编写 K8s manifest/编写 Deployment/Service/Ingress"时触发。
  按生态 Helm Chart 双 workload 模板生成 K8s manifest。
  支持原生 YAML 和 Helm Chart 两种方式。

triggers:
  - K8s
  - k8s 部署
  - 编写 manifest
  - Deployment
  - Service
  - Ingress
  - K8s YAML
  - helm chart
  - k8s-deployment

role: devops
phase: deployment

when-to-use: |
  为项目编写 K8s 部署文件（原生 YAML 或 Helm Chart）。
when-not-to-use: |
  - 仅编写 Dockerfile（用 dockerfile-writing）
  - 仅编写 docker-compose（用 docker-compose-design）
  - 仅使用 helm（用 helm-ops）

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - common-naming
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/kubernetes.md
  - wiki/_common/docker.md

produces:
  - K8s manifest（Deployment / Service / Ingress / ConfigMap / Secret）
  - 或 Helm Chart（Chart.yaml + values.yaml + templates/）

requires:
  - skill: dockerfile-writing
    condition: Dockerfile 已存在
    error: 无 Dockerfile，MUST 先调用 dockerfile-writing

trust-level: standard

require-confirm:
  - 生产部署 MUST 用户确认
  - namespace 选择 MUST 用户确认

mode: assist

category: deployment
stack: _common
priority: high
version: "0.3.0"
since: "2026-08-13"
---

# K8s 部署

> 按生态 Helm Chart 双 workload 模板生成 K8s 部署文件。

## 前置条件

- Dockerfile 已存在
- 已确定 K8s 集群和 namespace

## 执行步骤

### 第 1 步：确定部署方式

**MUST 询问用户**：

```
Q1: 部署方式？
    a) 原生 K8s YAML（简单场景）
    b) Helm Chart（推荐，生态标准）

Q2: 目标环境？
    a) 测试（test namespace）
    b) 预发（staging）
    c) 生产（prod namespace）
```

### 第 2 步：生成对应文件

#### 方式 A：原生 K8s YAML

```
k8s/
├── namespace.yaml
├── deployment-backend.yaml
├── service-backend.yaml
├── deployment-frontend.yaml
├── service-frontend.yaml
├── ingress.yaml
├── configmap.yaml
└── secret.yaml
```

#### 方式 B：Helm Chart（推荐）

按 `wiki/_common/kubernetes.md` 双 workload 模板生成：

```
helm/<chart-name>/
├── Chart.yaml
├── values.yaml
├── .helmignore
└── templates/
    ├── _helpers.tpl
    ├── deployment.yaml      # 双 workload（backend + frontend）
    ├── service.yaml
    ├── ingress.yaml
    ├── hpa.yaml
    ├── serviceaccount.yaml
    └── tests/test-connection.yaml
```

### 第 3 步：关键配置

#### 后端 Deployment 要点

```yaml
spec:
  replicas: 1
  template:
    spec:
      containers:
      - name: user-service
        image: registry.cn-hangzhou.aliyuncs.com/structured/user-service:1.2.0
        env:
        - name: APP_PATH
          value: /app/boot/app.jar
        - name: JAVA_OPTS
          value: -Xms256m -Xmx1024m
        - name: PARAMS
          value: -Dspring.profiles.active=pro
        ports:
        - containerPort: 8080
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 7777  # 生态约定
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 7777
          initialDelaySeconds: 30
          periodSeconds: 10
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
```

#### 前端 Deployment 要点

```yaml
spec:
  template:
    spec:
      containers:
      - name: user-ui
        image: registry.cn-hangzhou.aliyuncs.com/structured/user-ui:1.2.0
        env:
        - name: SCHEME
          value: https
        - name: SERVER_HOST
          value: api.prod.structured.cn
        - name: SERVER_PORT
          value: "443"
        ports:
        - containerPort: 80
```

### 第 4 步：验证

```bash
# 原生 YAML
kubectl apply -f k8s/ --dry-run=client
kubectl apply -f k8s/

# Helm
helm template <release> ./helm/<chart> -f values.yaml
helm upgrade --install <release> ./helm/<chart> -n <ns>
```

## 关键约束（MUST 遵守）

- ✅ **MUST** 含 `livenessProbe` + `readinessProbe`（后端 actuator 7777 端口）
- ✅ **MUST** 含 `resources.requests` 和 `resources.limits`
- ✅ **MUST** 镜像 tag 用具体版本号（**禁止 latest**）
- ✅ **MUST** 用 namespace 隔离环境
- ❌ **MUST NOT** 在 YAML 硬编码 Secrets（用 External Secrets / Sealed Secrets）
- ❌ **MUST NOT** 用 `hostNetwork: true` / `hostPID: true`

## 产出物

- K8s manifest 或 Helm Chart
- 部署验证报告

## 关联

- 前置：`dockerfile-writing`
- 后续：`k8s-verification` / `helm-ops`
- Wiki：`wiki/_common/kubernetes.md`
