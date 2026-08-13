---
name: iac-terraform
description: |
  当用户要求"Terraform/IaC/基础设施即代码/云资源编排"时触发。
  编写 Terraform 配置，管理云资源。

triggers:
  - Terraform
  - IaC
  - 基础设施即代码
  - 云资源编排
  - terraform
  - tf
  - 基础设施

role: devops
phase: support

when-to-use: |
  需要用 Terraform 管理云资源（VPC / ECS / RDS / OSS / K8s 集群）。
when-not-to-use: |
  - 仅部署应用（用 k8s-deployment / helm-ops）
  - 仅编写应用流水线（用 ci-pipeline-design）

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/ci-cd-pipeline.md

produces:
  - Terraform 配置文件（*.tf）
  - 状态后端配置
  - 变量与输出定义

requires: []

human-in-the-loop:
  - terraform apply MUST 用户确认
  - terraform destroy MUST 用户确认
  - 状态后端配置 MUST 用户确认

on-failure: |
  apply 失败 → terraform plan 分析差异
  状态锁冲突 → 协调后重试

mode: assist

category: deployment
stack: _common
priority: low
version: "0.3.0"
since: "2026-08-13"
---

# Terraform 使用

> 用 Terraform 管理云资源。**apply / destroy MUST 用户确认**。

## 项目结构

```
terraform/
├── main.tf              # 主入口
├── variables.tf         # 变量定义
├── outputs.tf           # 输出定义
├── terraform.tfvars     # 变量值（不入库）
├── backend.tf           # 状态后端
├── modules/             # 自定义模块
│   ├── vpc/
│   ├── ecs/
│   └── rds/
└── environments/        # 环境区分
    ├── dev/
    ├── staging/
    └── prod/
```

## 关键文件模板

### backend.tf（状态后端）

```hcl
terraform {
  backend "oss" {
    bucket = "structure-terraform-state"
    key    = "prod/terraform.tfstate"
    region = "cn-hangzhou"
  }
}
```

### variables.tf

```hcl
variable "env" {
  description = "Environment name"
  type        = string
}

variable "region" {
  description = "Cloud region"
  type        = string
  default     = "cn-hangzhou"
}
```

### main.tf

```hcl
provider "alicloud" {
  region = var.region
}

module "vpc" {
  source = "./modules/vpc"
  env    = var.env
}

module "ecs" {
  source = "./modules/ecs"
  env    = var.env
  vpc_id = module.vpc.vpc_id
}
```

### outputs.tf

```hcl
output "vpc_id" {
  value = module.vpc.vpc_id
}

output "ecs_public_ip" {
  value = module.ecs.public_ip
}
```

## 常用命令

```bash
# 初始化
terraform init

# 格式化
terraform fmt

# 校验
terraform validate

# 预览（不写）
terraform plan

# 应用（MUST 用户确认）
terraform apply

# 销毁（MUST 用户确认）
terraform destroy

# 查看状态
terraform show
terraform state list

# 查看输出
terraform output
```

## 关键约束

- ✅ **MUST** 状态远端存储（OSS / S3 / Terraform Cloud）
- ✅ **MUST** 用 `modules/` 复用配置
- ✅ **MUST** 用 `environments/` 区分环境
- ✅ **MUST** `apply` 前 MUST `plan` 预览
- ❌ **MUST NOT** 在 *.tf 硬编码 Secrets（用变量 + tfvars）
- ❌ **MUST NOT** 直接编辑远端状态

## 关联

- Wiki：`wiki/_common/ci-cd-pipeline.md`
- 相关：`helm-ops` / `kubectl-ops`
