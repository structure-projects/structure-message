---
description: |
triggers:
  - docker
  - docker 命令
  - docker 构建
  - docker 运行
  - docker 日志
  - docker push
  - docker pull
  - docker ps
  - docker images
  - 容器操作
role: devops
priority: medium
category: ci
stack: _common
alwaysApply: false
---


# docker CLI 使用

> 安全使用 docker 命令完成常见操作。**生产操作 MUST 用户确认**。

## 常用命令分类

### 镜像操作

```bash
# 构建镜像（含 tag）
docker build -t registry.cn-hangzhou.aliyuncs.com/structured/<service>:<version> .

# 同时打 version + latest
docker build -t <image>:<version> -t <image>:latest .

# 查看镜像
docker images

# 推送镜像
docker push <image>:<version>
docker push <image>:latest

# 拉取镜像
docker pull <image>:<version>

# 删除镜像
docker rmi <image>:<version>

# 镜像详情
docker inspect <image>:<version>

# 导出 / 导入
docker save -o image.tar <image>:<version>
docker load -i image.tar
```

### 容器操作

```bash
# 运行容器（后台 + 端口映射 + 名称）
docker run -d -p 8080:8080 --name <name> <image>:<version>

# 运行容器（含环境变量）
docker run -d -p 8080:8080 \
  -e JAVA_OPTS="-Xms256m -Xmx1024m" \
  -e PARAMS="-Dspring.profiles.active=pro" \
  --name <name> <image>:<version>

# 查看运行中容器
docker ps

# 查看所有容器（含停止）
docker ps -a

# 查看日志
docker logs <name>
docker logs -f <name>          # 跟随
docker logs --tail 100 <name>  # 最后 100 行

# 进入容器
docker exec -it <name> /bin/sh

# 停止 / 启动 / 重启
docker stop <name>
docker start <name>
docker restart <name>

# 删除容器
docker rm <name>
docker rm -f <name>  # 强制删除运行中的
```

### 清理操作（MUST 用户确认）

```bash
# 清理停止的容器
docker container prune

# 清理无用镜像
docker image prune

# 清理所有未使用资源（镜像 + 容器 + 网络 + 卷）
docker system prune

# 深度清理（含 volumes）
docker system prune -a --volumes
```

### 调试操作

```bash
# 查看容器详情
docker inspect <name>

# 查看资源占用
docker stats

# 查看进程
docker top <name>

# 复制文件
docker cp <name>:/path/to/file ./local-path
docker cp ./local-file <name>:/path/to/dest
```

### 网络与卷

```bash
# 查看网络
docker network ls
docker network inspect <network>

# 创建外部网络（生态约定）
docker network create structure-cloud-work

# 查看卷
docker volume ls
docker volume inspect <volume>
```

## 关键约束

- ✅ **MUST** 构建时打 `<version>` + `latest` 双 tag
- ✅ **MUST** 生产环境操作前用户确认
- ❌ **MUST NOT** 在生产环境用 `latest` tag
- ❌ **MUST NOT** 不加确认执行 `docker system prune -a --volumes`

## 常见问题

### 容器启动失败

```bash
docker logs <name>         # 看日志
docker inspect <name>      # 看配置
docker exec -it <name> sh  # 进容器排查
```

### 镜像太大

```bash
docker images --format "{{.Repository}}:{{.Tag}} {{.Size}}"
# 用 alpine 变体 + 多阶段构建减小体积
```

### 网络不通

```bash
docker network ls
docker network inspect structure-cloud-work
# 检查服务是否在同一网络
```

## 关联

- Wiki：`wiki/_common/docker.md`
- 相关：`dockerfile-writing` / `docker-compose-design` / `kubectl-ops`
