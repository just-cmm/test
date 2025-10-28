# 交通数据推送API

基于GA/T 1049.2-2013标准的交通数据接收服务，为交通系统提供标准化的数据推送接口。

## 项目概述

本项目实现了一个符合GA/T 1049.2-2013公共安全行业标准的交通数据推送API服务，支持：

- 单个交通数据推送
- 批量交通数据推送  
- 数据推送状态查询
- 完整的参数验证和异常处理
- 符合中国车牌号码规范的验证

## 技术栈

- **Java 21** - 编程语言
- **Spring Boot 3.2.0** - 应用框架
- **Spring Web** - REST API
- **Spring Validation** - 数据验证
- **Spring Data JPA** - 数据持久化
- **H2 Database** - 内存数据库（演示用）
- **Lombok** - 代码简化
- **Jackson** - JSON处理
- **JUnit 5** - 单元测试

## 项目结构

```
src/
├── main/
│   ├── java/com/example/maven/
│   │   ├── model/                    # 数据模型
│   │   │   ├── TrafficDataRequest.java    # 交通数据请求
│   │   │   ├── TrafficDataItem.java       # 交通数据项
│   │   │   └── ApiResponse.java           # 统一响应格式
│   │   ├── controller/               # 控制器
│   │   │   └── TrafficDataController.java # 交通数据API控制器
│   │   ├── service/                  # 服务层
│   │   │   └── TrafficDataService.java    # 交通数据服务
│   │   ├── exception/                # 异常处理
│   │   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │   └── TrafficDataApiApplication.java # 主应用类
│   └── resources/
│       └── application.yml           # 应用配置
└── test/
    └── java/com/example/maven/
        ├── controller/
        │   └── TrafficDataControllerTest.java # 控制器测试
        └── service/
            └── TrafficDataServiceTest.java    # 服务层测试
```

## 快速开始

### 1. 环境要求

- Java 21+
- Maven 3.6+

### 2. 编译和测试

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包应用
mvn clean package
```

### 3. 启动服务

```bash
# 方式1：使用启动脚本
./start.sh

# 方式2：直接运行JAR
java -jar target/traffic-data-api-0.0.1-SNAPSHOT.jar
```

### 4. 验证服务

访问健康检查接口：
```bash
curl http://localhost:8080/api/traffic/health
```

## API接口

### 1. 接收交通数据推送

**POST** `/api/traffic/data/push`

接收单个交通数据推送请求。

### 2. 批量接收交通数据推送

**POST** `/api/traffic/data/batch-push`

接收批量交通数据推送请求。

### 3. 查询数据推送状态

**GET** `/api/traffic/data/status/{packageId}`

查询指定数据包的推送状态。

### 4. 健康检查

**GET** `/api/traffic/health`

检查服务运行状态。

详细的API文档请参考 [API_DOCUMENTATION.md](API_DOCUMENTATION.md)。

## 数据模型

### TrafficDataRequest（交通数据请求）

包含以下主要字段：
- `packageId`: 数据包唯一标识符
- `sourceId`: 数据源标识
- `collectTime`: 数据采集时间
- `pushTime`: 数据推送时间
- `dataType`: 数据类型
- `dataVersion`: 数据版本
- `dataItems`: 数据项列表
- `signature`: 数据签名（可选）
- `remark`: 备注信息（可选）

### TrafficDataItem（交通数据项）

包含以下主要字段：
- `recordId`: 记录唯一标识
- `deviceId`: 设备编号
- `deviceLocation`: 设备位置
- `longitude`/`latitude`: 地理坐标
- `recordTime`: 记录时间
- `plateNumber`: 车牌号码（符合中国车牌规范）
- `vehicleType`: 车辆类型
- `vehicleColor`: 车辆颜色
- `speed`: 车速
- `direction`: 行驶方向
- `laneNumber`: 车道号
- `violationType`: 违法类型
- `violationCode`: 违法代码
- `imageUrl`/`videoUrl`: 媒体文件URL
- `confidence`: 置信度
- `extendedData`: 扩展字段（JSON格式）

## 特性

### 1. 数据验证

- 完整的参数验证（使用Bean Validation）
- 车牌号码格式验证（符合中国车牌规范）
- 时间合理性验证
- 数据包ID唯一性检查

### 2. 异常处理

- 全局异常处理器
- 统一的错误响应格式
- 详细的错误信息

### 3. 日志记录

- 完整的操作日志
- 错误日志记录
- 调试信息输出

### 4. 测试覆盖

- 单元测试覆盖
- 集成测试
- 边界条件测试

## 配置说明

主要配置项（application.yml）：

```yaml
server:
  port: 8080

spring:
  application:
    name: traffic-data-api
  datasource:
    url: jdbc:h2:mem:trafficdb
  jpa:
    hibernate:
      ddl-auto: create-drop

traffic:
  data:
    package-id-max-length: 32
    source-id-max-length: 16
    max-items-per-request: 1000
    retention-days: 30
    signature-validation-enabled: true
```

## 使用示例

### Java客户端示例

```java
// 创建请求数据
TrafficDataRequest request = new TrafficDataRequest();
request.setPackageId("PKG" + System.currentTimeMillis());
request.setSourceId("TRAFFIC_SYS_001");
request.setCollectTime(LocalDateTime.now().minusMinutes(5));
request.setPushTime(LocalDateTime.now());
request.setDataType("车辆通行");
request.setDataVersion("1.0");

// 创建数据项
TrafficDataItem item = new TrafficDataItem();
item.setRecordId("REC" + System.currentTimeMillis());
item.setDeviceId("DEV001");
item.setDeviceLocation("北京市朝阳区建国路1号");
item.setLongitude(116.3974);
item.setLatitude(39.9093);
item.setRecordTime(LocalDateTime.now());
item.setPlateNumber("京A12345");
item.setVehicleType("小型汽车");
item.setSpeed(60);

request.setDataItems(Arrays.asList(item));

// 发送请求
RestTemplate restTemplate = new RestTemplate();
String url = "http://localhost:8080/api/traffic/data/push";
ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url, request, ApiResponse.class);
```

### cURL示例

```bash
curl -X POST http://localhost:8080/api/traffic/data/push \
  -H "Content-Type: application/json" \
  -d '{
    "packageId": "PKG20231201001",
    "sourceId": "TRAFFIC_SYS_001",
    "collectTime": "2023-12-01 10:30:00",
    "pushTime": "2023-12-01 10:35:00",
    "dataType": "车辆通行",
    "dataVersion": "1.0",
    "dataItems": [
      {
        "recordId": "REC20231201001",
        "deviceId": "DEV001",
        "deviceLocation": "北京市朝阳区建国路1号",
        "longitude": 116.3974,
        "latitude": 39.9093,
        "recordTime": "2023-12-01 10:30:00",
        "plateNumber": "京A12345",
        "vehicleType": "小型汽车",
        "speed": 60
      }
    ]
  }'
```

## 部署说明

### 开发环境

1. 确保Java 21+环境
2. 运行 `mvn clean package` 构建项目
3. 运行 `java -jar target/traffic-data-api-0.0.1-SNAPSHOT.jar` 启动服务

### 生产环境

1. 配置生产数据库（MySQL/PostgreSQL等）
2. 调整JVM参数
3. 配置日志级别
4. 设置监控和告警
5. 配置负载均衡（如需要）

## 注意事项

1. **数据包ID唯一性**: 每个数据包ID必须唯一，重复推送相同ID会被拒绝
2. **时间格式**: 所有时间字段使用 `yyyy-MM-dd HH:mm:ss` 格式
3. **车牌号格式**: 车牌号必须符合中国车牌号码规范
4. **数据大小限制**: 单次推送建议不超过1000条记录
5. **网络超时**: 建议设置30秒的网络超时时间
6. **重试机制**: 建议实现指数退避的重试机制
7. **数据签名**: 生产环境建议启用数据签名验证

## 许可证

本项目采用MIT许可证。

## 联系方式

如有问题或建议，请联系开发团队。