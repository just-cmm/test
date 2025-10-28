package com.example.maven.controller;

import com.example.maven.model.ApiResponse;
import com.example.maven.model.TrafficDataRequest;
import com.example.maven.service.TrafficDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 交通数据推送API控制器
 * 提供符合GA/T 1049.2-2013标准的交通数据接收接口
 */
@Slf4j
@RestController
@RequestMapping("/api/traffic")
@Validated
public class TrafficDataController {
    
    @Autowired
    private TrafficDataService trafficDataService;
    
    /**
     * 接收交通数据推送
     * 
     * @param request 交通数据请求
     * @return 处理结果
     */
    @PostMapping("/data/push")
    public ResponseEntity<ApiResponse<Map<String, Object>>> pushTrafficData(
            @Valid @RequestBody TrafficDataRequest request) {
        
        try {
            log.info("接收到交通数据推送请求，数据包ID: {}, 数据源: {}, 数据类型: {}", 
                    request.getPackageId(), request.getSourceId(), request.getDataType());
            
            // 验证数据包ID唯一性
            if (trafficDataService.isPackageIdExists(request.getPackageId())) {
                log.warn("数据包ID已存在: {}", request.getPackageId());
                return ResponseEntity.badRequest()
                        .body(ApiResponse.badRequest("数据包ID已存在，请使用唯一标识符"));
            }
            
            // 处理交通数据
            Map<String, Object> result = trafficDataService.processTrafficData(request);
            
            log.info("交通数据推送处理成功，数据包ID: {}", request.getPackageId());
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (IllegalArgumentException e) {
            log.error("数据验证失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("数据验证失败: " + e.getMessage()));
        } catch (Exception e) {
            log.error("处理交通数据时发生错误，数据包ID: {}", request.getPackageId(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.serverError("服务器内部错误，请稍后重试"));
        }
    }
    
    /**
     * 批量接收交通数据推送
     * 
     * @param requests 交通数据请求列表
     * @return 处理结果
     */
    @PostMapping("/data/batch-push")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchPushTrafficData(
            @Valid @RequestBody java.util.List<TrafficDataRequest> requests) {
        
        try {
            log.info("接收到批量交通数据推送请求，数据包数量: {}", requests.size());
            
            Map<String, Object> result = trafficDataService.processBatchTrafficData(requests);
            
            log.info("批量交通数据推送处理完成");
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            log.error("处理批量交通数据时发生错误", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.serverError("服务器内部错误，请稍后重试"));
        }
    }
    
    /**
     * 查询数据推送状态
     * 
     * @param packageId 数据包ID
     * @return 推送状态
     */
    @GetMapping("/data/status/{packageId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPushStatus(
            @PathVariable @NotBlank String packageId) {
        
        try {
            Map<String, Object> status = trafficDataService.getPushStatus(packageId);
            return ResponseEntity.ok(ApiResponse.success(status));
        } catch (Exception e) {
            log.error("查询推送状态时发生错误，数据包ID: {}", packageId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.serverError("查询失败，请稍后重试"));
        }
    }
    
    /**
     * 健康检查接口
     * 
     * @return 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "交通数据推送服务",
                "version", "1.0.0",
                "standard", "GA/T 1049.2-2013"
        );
        return ResponseEntity.ok(ApiResponse.success(health));
    }
}