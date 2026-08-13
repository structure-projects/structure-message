---
name: maven-publish
description: |
  当用户要求"发布 Maven 包/发 maven/maven deploy/发布到 Maven Central"时触发。
  按生态规范发布 Java 包到 Maven Central（Sonatype OSSRH）。

triggers:
  - 发布 Maven
  - 发 maven
  - maven deploy
  - Maven Central
  - 发布到 Maven Central
  - maven publish

role: devops
phase: deployment

when-to-use: |
  发布 Java 包到 Maven Central。
when-not-to-use: |
  - 仅本地构建（用 mvn package）
  - 仅发布到内部 Nexus

allowed-tools: Bash, Read, Write, Edit

related-rules:
  - common-version-management
  - common-project-stack-detection

reads-before-action:
  - wiki/_common/maven-publish.md
  - wiki/_common/version-management.md

produces:
  - 发布的 Maven 包（Maven Central）
  - 更新 pom.xml version
  - Git Tag

requires:
  - skill: ci-gate
    condition: CI 通过
    error: CI 未通过

trust-level: standard

require-confirm:
  - maven deploy MUST 用户确认（生产操作）
  - 版本号 MUST 用户确认
  - GPG 签名 MUST 用户确认

on-failure: |
  发布失败 → 检查 OSSRH 凭据 / GPG 签名
  版本冲突 → 升版本号后重试

mode: assist

category: deployment
stack: _common
priority: high
version: "0.3.0"
since: "2026-08-13"
---

# Maven 发布

> 按生态规范发布 Java 包到 Maven Central。**MUST 用户确认**。

## 前置条件

- CI 通过
- pom.xml 含 `distributionManagement` + `nexus-staging-maven-plugin` + `maven-gpg-plugin`
- OSSRH 凭据已配置
- GPG 密钥已配置

## 关键配置（pom.xml）

```xml
<distributionManagement>
  <snapshotRepository>
    <id>oss</id>
    <url>https://central.sonatype.com/repository/maven-snapshots/</url>
  </snapshotRepository>
  <repository>
    <id>oss</id>
    <url>https://central.sonatype.com/service/local/staging/deploy/maven2/</url>
  </repository>
</distributionManagement>

<build>
  <plugins>
    <plugin>
      <groupId>org.sonatype.plugins</groupId>
      <artifactId>nexus-staging-maven-plugin</artifactId>
      <version>1.6.13</version>
    </plugin>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-gpg-plugin</artifactId>
      <version>1.5</version>
      <executions>
        <execution>
          <phase>verify</phase>
          <goals><goal>sign</goal></goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

## 执行步骤

### 第 1 步：发布前检查

```bash
# 确认版本号
grep "<revision>" pom.xml

# 确认 profile
mvn help:active-profiles
```

### 第 2 步：本地构建 + 测试

```bash
mvn clean install
mvn clean test
```

### 第 3 步：发布（MUST 用户确认）

```bash
# 使用 release,oss 双 profile + -Drevision 属性化版本
mvn clean deploy -P release,oss -Drevision=1.2.0
```

### 第 4 步：验证

```bash
# 在 Sonatype 查看
# https://central.sonatype.com/

# 在测试项目里引用验证
mvn dependency:get -DartifactId=cn.structured:structure-infra:1.2.0
```

### 第 5 步：打 Tag

```bash
git tag -a v1.2.0 -m "Release structure-infra v1.2.0"
git push origin v1.2.0
```

## 关键约束

- ✅ **MUST** 用 `release,oss` 双 profile
- ✅ **MUST** 用 `-Drevision=` 属性化版本
- ✅ **MUST** GPG 签名
- ❌ **MUST NOT** 在 pom.xml 硬编码 Secrets
- ❌ **MUST NOT** 跳过 GPG 签名

## 关联

- 前置：`ci-gate`
- 相关：`gh-release` / `npm-publish`
- Wiki：`wiki/_common/maven-publish.md` `wiki/_common/version-management.md`
