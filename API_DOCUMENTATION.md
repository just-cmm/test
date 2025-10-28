# 交通数据推送API文档

## 概述

本API提供符合GA/T 1049.2-2013标准的交通数据接收服务，供交通系统调用推送数据。

## 基础信息

- **服务名称**: 交通数据推送服务
- **版本**: 1.0.0
- **标准**: GA/T 1049.2-2013
- **基础URL**: `http://localhost:8080/api/traffic`

## API接口

### 1. 接收交通数据推送

**接口地址**: `POST /api/traffic/data/push`

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
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
      "vehicleColor": "白色",
      "speed": 60,
      "direction": "东",
      "laneNumber": 1,
      "violationType": "超速行驶",
      "violationCode": "1352",
      "imageUrl": "http://example.com/images/20231201/001.jpg",
      "videoUrl": "http://example.com/videos/20231201/001.mp4",
      "confidence": 95,
      "extendedData": "{\"weather\":\"晴\",\"temperature\":\"15℃\"}"
    }
  ],
  "signature": "abc123def456...",
  "remark": "测试数据推送"
}
```

**响应示例**:
```json
{
  "code": "200",
  "message": "操作成功",
  "data": {
    "packageId": "PKG20231201001",
    "status": "SUCCESS",
    "processedTime": "2023-12-01T10:35:15",
    "processedCount": 1,
    "message": "数据推送处理成功"
  },
  "timestamp": 1701405315000
}
```

### 2. 批量接收交通数据推送

**接口地址**: `POST /api/traffic/data/batch-push`

**请求体**: 交通数据请求数组
```json
[
  {
    "packageId": "PKG20231201001",
    "sourceId": "TRAFFIC_SYS_001",
    // ... 其他字段同单个推送
  },
  {
    "packageId": "PKG20231201002",
    "sourceId": "TRAFFIC_SYS_001",
    // ... 其他字段
  }
]
```

**响应示例**:
```json
{
  "code": "200",
  "message": "操作成功",
  "data": {
    "totalCount": 2,
    "successCount": 2,
    "failureCount": 0,
    "processedResults": [
      {
        "packageId": "PKG20231201001",
        "status": "SUCCESS",
        "processedTime": "2023-12-01T10:35:15",
        "processedCount": 1
      },
      {
        "packageId": "PKG20231201002",
        "status": "SUCCESS",
        "processedTime": "2023-12-01T10:35:16",
        "processedCount": 1
      }
    ],
    "processedTime": "2023-12-01T10:35:16"
  },
  "timestamp": 1701405316000
}
```

### 3. 查询数据推送状态

**接口地址**: `GET /api/traffic/data/status/{packageId}`

**路径参数**:
- `packageId`: 数据包ID

**响应示例**:
```json
{
  "code": "200",
  "message": "操作成功",
  "data": {
    "packageId": "PKG20231201001",
    "status": "SUCCESS",
    "processedTime": "2023-12-01T10:35:15",
    "processedCount": 1,
    "message": "数据推送处理成功"
  },
  "timestamp": 1701405315000
}
```

### 4. 健康检查

**接口地址**: `GET /api/traffic/health`

**响应示例**:
```json
{
  "code": "200",
  "message": "操作成功",
  "data": {
    "status": "UP",
    "service": "交通数据推送服务",
    "version": "1.0.0",
    "standard": "GA/T 1049.2-2013"
  },
  "timestamp": 1701405315000
}
```

## 数据字段说明

### TrafficDataRequest（交通数据请求）

| 字段名 | 类型 | 必填 | 长度限制 | 说明 |
|--------|------|------|----------|------|
| packageId | String | 是 | 32字符 | 数据包唯一标识符 |
| sourceId | String | 是 | 16字符 | 数据源标识 |
| collectTime | DateTime | 是 | - | 数据采集时间 |
| pushTime | DateTime | 是 | - | 数据推送时间 |
| dataType | String | 是 | 20字符 | 数据类型 |
| dataVersion | String | 是 | 10字符 | 数据版本 |
| dataItems | Array | 是 | - | 数据项列表 |
| signature | String | 否 | 256字符 | 数据签名 |
| remark | String | 否 | 500字符 | 备注信息 |

### TrafficDataItem（交通数据项）

| 字段名 | 类型 | 必填 | 长度限制 | 说明 |
|--------|------|------|----------|------|
| recordId | String | 是 | 32字符 | 记录唯一标识 |
| deviceId | String | 是 | 20字符 | 设备编号 |
| deviceLocation | String | 是 | 100字符 | 设备位置 |
| longitude | Double | 是 | - | 经度 |
| latitude | Double | 是 | - | 纬度 |
| recordTime | DateTime | 是 | - | 记录时间 |
| plateNumber | String | 否 | - | 车牌号码（需符合中国车牌格式） |
| vehicleType | String | 否 | 20字符 | 车辆类型 |
| vehicleColor | String | 否 | 10字符 | 车辆颜色 |
| speed | Integer | 否 | - | 车速（km/h） |
| direction | String | 否 | 10字符 | 行驶方向 |
| laneNumber | Integer | 否 | - | 车道号 |
| violationType | String | 否 | 50字符 | 违法类型 |
| violationCode | String | 否 | 10字符 | 违法代码 |
| imageUrl | String | 否 | 500字符 | 图片URL |
| videoUrl | String | 否 | 500字符 | 视频URL |
| confidence | Integer | 否 | - | 置信度（0-100） |
| extendedData | String | 否 | 1000字符 | 扩展字段（JSON格式） |

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 500 | 服务器内部错误 |

## 使用示例

### Java客户端示例

```java
// 使用Spring RestTemplate
RestTemplate restTemplate = new RestTemplate();

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
String url = "http://localhost:8080/api/traffic/data/push";
ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
    url, request, ApiResponse.class);

System.out.println("响应状态: " + response.getStatusCode());
System.out.println("响应内容: " + response.getBody());
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

## 注意事项

1. **数据包ID唯一性**: 每个数据包ID必须唯一，重复推送相同ID会被拒绝
2. **时间格式**: 所有时间字段使用 `yyyy-MM-dd HH:mm:ss` 格式
3. **车牌号格式**: 车牌号必须符合中国车牌号码规范
4. **数据大小限制**: 单次推送建议不超过1000条记录
5. **网络超时**: 建议设置30秒的网络超时时间
6. **重试机制**: 建议实现指数退避的重试机制
7. **数据签名**: 生产环境建议启用数据签名验证

## 部署说明

1. 确保Java 8+环境
2. 运行 `mvn clean package` 构建项目
3. 运行 `java -jar target/traffic-data-api-0.0.1-SNAPSHOT.jar` 启动服务
4. 访问 `http://localhost:8080/api/traffic/health` 检查服务状态