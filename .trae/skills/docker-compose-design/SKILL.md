---
name: docker-compose-design
description: |
  当用户要求"写 docker-compose/编排服务/编写部署文件"时触发。
  按生态规范生成 docker-compose.yml：统一网络 + 三件套 + 健康检查。

triggers:
  - 写 docker-compose
  - 编排服务
  - 编写部署文件
  - docker compose
  - 多服务部署
  - 本地编排

role: devops
phase: ci

when-to-use: |
  为项目编写 docker-compose.yml（多服务编排）。
when-not-to-use: |
  - 仅写 Dockerfile（用 dockerfile-writing）
  - K8s 部署（用 k8s-deployment）

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - common-naming
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/docker.md
  - wiki/_common/ci-cd-pipeline.md

produces:
  - docker-compose.yml
  - .env 模板

requires:
  - skill: dockerfile-writing
    condition: 各服务 Dockerfile 已存在
    error: 无 Dockerfile，MUST 先调用 dockerfile-writing

human-in-the-loop:
  - 服务清单 MUST 用户确认
  - 端口映射 MUST 用户确认
  - 环境变量 MUST 用户确认

on-failure: |
  服务启动失败 → 分析日志，修复后重试
  健康检查失败 → 检查 liveness.sh 或 wget 脚本

mode: assist

category: ci
stack: _common
priority: high
version: "0.3.0"
since: "2026-08-13"
---

# docker-compose 设计

> 按生态规范生成 docker-compose.yml。**统一网络 + 三件套 + 健康检查**。

## 前置条件

- 各服务 Dockerfile 已存在
- 已确定服务清单

## 执行步骤

### 第 1 步：确定服务清单

**MUST 询问用户**：

```
Q1: 包含哪些服务？
    例如：
    - user-service（后端）
    - user-ui（前端）
    - mysql
    - redis
    - nacos

Q2: 端口映射？
    例如：
    - user-service: 8080
    - user-ui: 80
```

### 第 2 步：生成 docker-compose.yml

按 `wiki/_common/docker.md` 模板生成。

**骨架**：

```yaml
version: "3.8"

services:
  # 后端服务
  user-service:
    image: registry.cn-hangzhou.aliyuncs.com/structured/user-service:1.2.0
    restart: always
    hostname: user-service
    container_name: user-service
    env_file: [.env]
    deploy:
      restart_policy: { condition: on-failure }
      replicas: 1
    networks: [structure-cloud-work]
    environment:
      - APP_PATH=/app/boot/app.jar
      - JAVA_OPTS=-Xms256m -Xmx1024m
      - PARAMS=-Dfile.encoding=UTF-8 -Dspring.profiles.active=pro -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Shanghai
    healthcheck:
      test: ["CMD", "/bin/sh", "/app/liveness.sh"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    ports:
      - "8080:8080"

  # 前端服务
  user-ui:
    image: registry.cn-hangzhou.aliyuncs.com/structured/user-ui:1.2.0
    restart: always
    hostname: user-ui
    container_name: user-ui
    env_file: [.env]
    networks: [structure-cloud-work]
    environment:
      - SCHEME=https
      - SERVER_HOST=api.prod.structured.cn
      - SERVER_PORT=443
    healthcheck:
      test: ["CMD", "wget", "--spider", "-q", "http://localhost/"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 10s
    ports:
      - "80:80"

networks:
  structure-cloud-work:
    external: true
```

### 第 3 步：生成 .env 模板

```bash
# .env.example
TZ=Asia/Shanghai

# 数据库
MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_USER=root
MYSQL_PASSWORD=<填入>

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# Nacos
NACOS_ADDR=nacos:8848
```

### 第 4 步：本地验证

```bash
# 启动
docker-compose up -d

# 查看状态
docker-compose ps

# 查看日志
docker-compose logs -f user-service

# 健康检查
docker-compose ps  # 看 STATUS 列应为 healthy

# 停止
docker-compose down
```

## 关键约束（MUST 遵守）

- ✅ **MUST** 使用 `version: "3.8"`
- ✅ **MUST** 所有服务接入 `structure-cloud-work`（`external: true`）
- ✅ **MUST** 后端用 `liveness.sh` 健康检查
- ✅ **MUST** 前端用 `wget --spider` 健康检查
- ✅ **MUST** 后端传 `JAVA_OPTS` / `PARAMS` / `APP_PATH` 三件套
- ✅ **MUST** 前端传 `SCHEME` / `SERVER_HOST` / `SERVER_PORT`
- ✅ **MUST** 使用 `env_file: .env`
- ❌ **MUST NOT** 硬编码 Secrets
- ❌ **MUST NOT** 用 `latest` tag（生产环境）

## 产出物

- docker-compose.yml
- .env.example

## 完成标准

- docker-compose.yml 语法正确
- `docker-compose config` 验证通过
- `docker-compose up -d` 启动成功
- 所有服务健康检查通过

## 关联

- 前置：`dockerfile-writing`
- 后续：`ci-pipeline-design` / `k8s-deployment`
- Wiki：`wiki/_common/docker.md`
