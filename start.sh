#!/bin/bash

echo "启动交通数据推送API服务..."
echo "服务地址: http://localhost:8080"
echo "健康检查: http://localhost:8080/api/traffic/health"
echo "API文档: 请参考 API_DOCUMENTATION.md"
echo ""

# 启动Spring Boot应用
java -jar target/traffic-data-api-0.0.1-SNAPSHOT.jar