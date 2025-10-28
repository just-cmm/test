package com.example.maven.service;

import com.example.maven.model.TrafficDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 交通数据服务
 * 处理交通数据的业务逻辑
 */
@Slf4j
@Service
public class TrafficDataService {
    
    // 用于存储数据包ID，实际项目中应使用数据库
    private final Set<String> processedPackageIds = ConcurrentHashMap.newKeySet();
    
    // 用于存储推送状态，实际项目中应使用数据库
    private final Map<String, Map<String, Object>> pushStatusMap = new ConcurrentHashMap<>();
    
    /**
     * 处理单个交通数据推送
     * 
     * @param request 交通数据请求
     * @return 处理结果
     */
    public Map<String, Object> processTrafficData(TrafficDataRequest request) {
        // 验证数据完整性
        validateTrafficData(request);
        
        // 模拟数据处理逻辑
        processDataItems(request.getDataItems());
        
        // 记录处理状态
        String packageId = request.getPackageId();
        processedPackageIds.add(packageId);
        
        Map<String, Object> status = new HashMap<>();
        status.put("packageId", packageId);
        status.put("status", "SUCCESS");
        status.put("processedTime", LocalDateTime.now());
        status.put("processedCount", request.getDataItems().size());
        status.put("message", "数据推送处理成功");
        
        pushStatusMap.put(packageId, status);
        
        log.info("交通数据处理完成，数据包ID: {}, 处理记录数: {}", 
                packageId, request.getDataItems().size());
        
        return status;
    }
    
    /**
     * 处理批量交通数据推送
     * 
     * @param requests 交通数据请求列表
     * @return 处理结果
     */
    public Map<String, Object> processBatchTrafficData(List<TrafficDataRequest> requests) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> processedResults = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        
        for (TrafficDataRequest request : requests) {
            try {
                Map<String, Object> processResult = processTrafficData(request);
                processedResults.add(processResult);
                successCount++;
            } catch (Exception e) {
                log.error("处理数据包失败: {}", request.getPackageId(), e);
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("packageId", request.getPackageId());
                errorResult.put("status", "FAILED");
                errorResult.put("error", e.getMessage());
                processedResults.add(errorResult);
                failureCount++;
            }
        }
        
        result.put("totalCount", requests.size());
        result.put("successCount", successCount);
        result.put("failureCount", failureCount);
        result.put("processedResults", processedResults);
        result.put("processedTime", LocalDateTime.now());
        
        return result;
    }
    
    /**
     * 获取推送状态
     * 
     * @param packageId 数据包ID
     * @return 推送状态
     */
    public Map<String, Object> getPushStatus(String packageId) {
        Map<String, Object> status = pushStatusMap.get(packageId);
        if (status == null) {
            status = new HashMap<>();
            status.put("packageId", packageId);
            status.put("status", "NOT_FOUND");
            status.put("message", "未找到对应的数据包");
        }
        return status;
    }
    
    /**
     * 检查数据包ID是否已存在
     * 
     * @param packageId 数据包ID
     * @return 是否存在
     */
    public boolean isPackageIdExists(String packageId) {
        return processedPackageIds.contains(packageId);
    }
    
    /**
     * 验证交通数据
     * 
     * @param request 交通数据请求
     */
    private void validateTrafficData(TrafficDataRequest request) {
        // 验证数据包ID格式
        if (request.getPackageId() == null || request.getPackageId().trim().isEmpty()) {
            throw new IllegalArgumentException("数据包ID不能为空");
        }
        
        // 验证数据源ID
        if (request.getSourceId() == null || request.getSourceId().trim().isEmpty()) {
            throw new IllegalArgumentException("数据源ID不能为空");
        }
        
        // 验证数据类型
        if (request.getDataType() == null || request.getDataType().trim().isEmpty()) {
            throw new IllegalArgumentException("数据类型不能为空");
        }
        
        // 验证数据项
        if (request.getDataItems() == null || request.getDataItems().isEmpty()) {
            throw new IllegalArgumentException("数据项不能为空");
        }
        
        // 验证时间合理性
        if (request.getCollectTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("数据采集时间不能晚于当前时间");
        }
        
        if (request.getPushTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("数据推送时间不能晚于当前时间");
        }
        
        // 验证数据项内容
        for (int i = 0; i < request.getDataItems().size(); i++) {
            var item = request.getDataItems().get(i);
            if (item.getRecordId() == null || item.getRecordId().trim().isEmpty()) {
                throw new IllegalArgumentException("第" + (i + 1) + "条记录的记录ID不能为空");
            }
            if (item.getDeviceId() == null || item.getDeviceId().trim().isEmpty()) {
                throw new IllegalArgumentException("第" + (i + 1) + "条记录的设备ID不能为空");
            }
            if (item.getRecordTime() == null) {
                throw new IllegalArgumentException("第" + (i + 1) + "条记录的记录时间不能为空");
            }
        }
    }
    
    /**
     * 处理数据项
     * 
     * @param dataItems 数据项列表
     */
    private void processDataItems(List<com.example.maven.model.TrafficDataItem> dataItems) {
        for (var item : dataItems) {
            // 模拟数据处理逻辑
            // 实际项目中这里会进行：
            // 1. 数据清洗和标准化
            // 2. 数据存储到数据库
            // 3. 触发后续业务逻辑
            // 4. 发送通知等
            
            log.debug("处理数据项: 记录ID={}, 设备ID={}, 车牌号={}", 
                    item.getRecordId(), item.getDeviceId(), item.getPlateNumber());
        }
    }
}