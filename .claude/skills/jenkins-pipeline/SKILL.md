---
name: jenkins-pipeline
description: |
  当用户要求"Jenkins 流水线/Jenkinsfile/Jenkins 构建"时触发。
  编写 Jenkins 声明式流水线，替代 GitHub Actions 场景。

triggers:
  - Jenkins
  - Jenkinsfile
  - Jenkins 流水线
  - Jenkins 构建
  - jenkins pipeline

role: devops
phase: ci

when-to-use: |
  项目使用 Jenkins 而非 GitHub Actions 作为 CI/CD 工具。
when-not-to-use: |
  - 项目用 GitHub Actions（用 ci-pipeline-design）
  - 项目用云效（用 yunxiao-pipeline）

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - common-git
  - common-version-management
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/ci-cd-pipeline.md

produces:
  - Jenkinsfile（声明式）
  - 必要的共享库引用

requires: []

human-in-the-loop:
  - Jenkins 凭据配置 MUST 用户确认
  - 生产部署 MUST 用户确认（input step）

mode: assist

category: ci
stack: _common
priority: medium
version: "0.3.0"
since: "2026-08-13"
---

# Jenkins 流水线

> 编写 Jenkins 声明式流水线。**生产部署 MUST 用户确认（input step）**。

## 声明式 Jenkinsfile 模板

### 后端（Java / Spring Boot）

```groovy
pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3.9'
    }

    environment {
        REGISTRY = 'registry.cn-hangzhou.aliyuncs.com'
        NAMESPACE = 'structured'
        IMAGE = "${REGISTRY}/${NAMESPACE}/${JOB_NAME}"
        VERSION = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco()
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Image') {
            steps {
                sh "docker build -t ${IMAGE}:${VERSION} -t ${IMAGE}:latest ."
            }
        }

        stage('Push Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'aliyun-acr',
                    usernameVariable: 'USERNAME',
                    passwordVariable: 'PASSWORD'
                )]) {
                    sh 'echo $PASSWORD | docker login --username=$USERNAME --password-stdin $REGISTRY'
                    sh "docker push ${IMAGE}:${VERSION}"
                    sh "docker push ${IMAGE}:latest"
                }
            }
        }

        stage('Deploy to Prod') {
            // 生产部署 MUST 用户确认
            input {
                message "Deploy to production?"
                ok "Deploy"
            }
            steps {
                sh "kubectl set image deployment/${JOB_NAME} ${JOB_NAME}=${IMAGE}:${VERSION} -n prod"
                sh "kubectl rollout status deployment/${JOB_NAME} -n prod"
            }
        }
    }

    post {
        success {
            echo '✓ Pipeline succeeded'
        }
        failure {
            echo '✗ Pipeline failed'
        }
        always {
            cleanWs()
        }
    }
}
```

### 前端（Vue / React）

```groovy
pipeline {
    agent any

    tools {
        nodejs 'node20'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install') {
            steps {
                sh 'npm ci'
            }
        }

        stage('Lint + Test') {
            parallel {
                stage('Lint') {
                    steps {
                        sh 'npm run lint'
                    }
                }
                stage('Test') {
                    steps {
                        sh 'npm run test'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                sh 'npm run build'
            }
        }

        stage('Build Image') {
            steps {
                sh "docker build -t ${IMAGE}:${VERSION} -t ${IMAGE}:latest ."
            }
        }

        stage('Push + Deploy') {
            steps {
                // ...
            }
        }
    }
}
```

## 关键约定

- ✅ **MUST** 用声明式（`pipeline { ... }`）而非脚本式
- ✅ **MUST** 生产部署用 `input` 步骤确认
- ✅ **MUST** 用 `withCredentials` 管理凭据
- ✅ **MUST** 用 `post` 块做清理
- ✅ **MUST** `disableConcurrentBuilds` 防止并发
- ✅ **MUST** `buildDiscarder` 保留历史

## 关联

- Wiki：`wiki/_common/ci-cd-pipeline.md`
- 相关：`ci-pipeline-design` / `yunxiao-pipeline`
