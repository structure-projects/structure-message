---
name: yunxiao-pipeline
description: |
  当用户要求"云效流水线/阿里云效/云效 CI/CD"时触发。
  编写阿里云效流水线 YAML。

triggers:
  - 云效
  - 阿里云效
  - 云效流水线
  - yunxiao
  - 云效 CI

role: devops
phase: ci

when-to-use: |
  项目使用阿里云效作为 CI/CD 工具。
when-not-to-use: |
  - 项目用 GitHub Actions（用 ci-pipeline-design）
  - 项目用 Jenkins（用 jenkins-pipeline）

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - common-git
  - common-version-management
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/ci-cd-pipeline.md

produces:
  - 云效流水线 YAML
  - 变量与凭据配置说明

requires: []

human-in-the-loop:
  - 生产部署 MUST 用户确认
  - 凭据配置 MUST 用户确认

mode: assist

category: ci
stack: _common
priority: medium
version: "0.3.0"
since: "2026-08-13"
---

# 云效流水线

> 编写阿里云效流水线。**生产部署 MUST 用户确认**。

## 云效流水线 YAML 模板

### 后端（Java + Docker + K8s）

```yaml
# .yunxiao/pipeline.yml
version: '1.0'
name: user-service-pipeline

stages:
  - name: build
    displayName: 构建
    jobs:
      - name: maven-build
        displayName: Maven 构建
        component: MavenBuild
        inputs:
          jdkVersion: '17'
          mavenVersion: '3.9'
          buildCommand: |
            mvn clean package -DskipTests
          artifactPath: target/*.jar

  - name: docker
    displayName: Docker 镜像
    jobs:
      - name: docker-build
        displayName: Docker 构建推送
        component: DockerBuild
        inputs:
          dockerfile: Dockerfile
          registry: registry.cn-hangzhou.aliyuncs.com
          namespace: structured
          imageName: user-service
          imageTag: ${CI_COMMIT_REF_NAME}-${CI_BUILD_NUMBER}
          username: ${DOCKER_USERNAME}
          password: ${DOCKER_PASSWORD}

  - name: deploy-test
    displayName: 部署测试环境
    jobs:
      - name: k8s-deploy-test
        displayName: K8s 部署（测试）
        component: KubernetesDeploy
        inputs:
          namespace: test
          deployment: user-service
          image: registry.cn-hangzhou.aliyuncs.com/structured/user-service:${CI_COMMIT_REF_NAME}-${CI_BUILD_NUMBER}

  - name: approval
    displayName: 生产审批
    jobs:
      - name: manual-approval
        displayName: 人工审批
        component: ManualApproval
        inputs:
          approvers: ['<user1>', '<user2>']
          message: 是否部署到生产环境？

  - name: deploy-prod
    displayName: 部署生产环境
    jobs:
      - name: k8s-deploy-prod
        displayName: K8s 部署（生产）
        component: KubernetesDeploy
        inputs:
          namespace: prod
          deployment: user-service
          image: registry.cn-hangzhou.aliyuncs.com/structured/user-service:${CI_COMMIT_REF_NAME}-${CI_BUILD_NUMBER}
```

### 前端（Node + Nginx + K8s）

```yaml
version: '1.0'
name: user-ui-pipeline

stages:
  - name: build
    displayName: 构建
    jobs:
      - name: node-build
        displayName: Node 构建
        component: NodeBuild
        inputs:
          nodeVersion: '20'
          buildCommand: |
            npm ci
            npm run build
          artifactPath: dist/

  - name: docker
    displayName: Docker 镜像
    jobs:
      - name: docker-build
        displayName: Docker 构建推送
        component: DockerBuild
        inputs:
          dockerfile: Dockerfile
          registry: registry.cn-hangzhou.aliyuncs.com
          namespace: structured
          imageName: user-ui
          imageTag: ${CI_COMMIT_REF_NAME}-${CI_BUILD_NUMBER}

  # 后续部署...
```

## 关键约定

- ✅ **MUST** 生产部署前用 `ManualApproval` 人工审批
- ✅ **MUST** 镜像 tag 含 `${CI_COMMIT_REF_NAME}-${CI_BUILD_NUMBER}`
- ✅ **MUST** 凭据用云效变量管理（不写死）

## 关联

- Wiki：`wiki/_common/ci-cd-pipeline.md`
- 相关：`ci-pipeline-design` / `jenkins-pipeline`
