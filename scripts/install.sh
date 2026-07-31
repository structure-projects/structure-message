#!/usr/bin/env bash
#在本地仓库安装.RELEASE
version=$1
if [ -z "$version" ]; then
    version=2.0.0
fi
mvn clean install -f ../structure-message-dependencies/pom.xml -Dmaven.test.skip=true -Drevision=$version
