package com.example.maven.controller;

import com.example.maven.model.TrafficDataItem;
import com.example.maven.model.TrafficDataRequest;
import com.example.maven.service.TrafficDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 交通数据控制器测试类
 */
@RunWith(SpringRunner.class)
@WebMvcTest(TrafficDataController.class)
public class TrafficDataControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TrafficDataService trafficDataService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private TrafficDataRequest validRequest;
    private TrafficDataItem validDataItem;
    
    @Before
    public void setUp() {
        // 创建有效的测试数据
        validDataItem = new TrafficDataItem();
        validDataItem.setRecordId("REC001");
        validDataItem.setDeviceId("DEV001");
        validDataItem.setDeviceLocation("北京市朝阳区建国路1号");
        validDataItem.setLongitude(116.3974);
        validDataItem.setLatitude(39.9093);
        validDataItem.setRecordTime(LocalDateTime.now());
        validDataItem.setPlateNumber("京A12345");
        validDataItem.setVehicleType("小型汽车");
        validDataItem.setVehicleColor("白色");
        validDataItem.setSpeed(60);
        validDataItem.setDirection("东");
        validDataItem.setLaneNumber(1);
        validDataItem.setConfidence(95);
        
        validRequest = new TrafficDataRequest();
        validRequest.setPackageId("PKG001");
        validRequest.setSourceId("SRC001");
        validRequest.setCollectTime(LocalDateTime.now().minusMinutes(5));
        validRequest.setPushTime(LocalDateTime.now());
        validRequest.setDataType("车辆通行");
        validRequest.setDataVersion("1.0");
        validRequest.setDataItems(Arrays.asList(validDataItem));
        validRequest.setRemark("测试数据");
    }
    
    @Test
    public void testPushTrafficData_Success() throws Exception {
        // 模拟服务层返回
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("packageId", "PKG001");
        mockResult.put("status", "SUCCESS");
        mockResult.put("processedCount", 1);
        
        when(trafficDataService.isPackageIdExists(anyString())).thenReturn(false);
        when(trafficDataService.processTrafficData(any(TrafficDataRequest.class))).thenReturn(mockResult);
        
        mockMvc.perform(post("/api/traffic/data/push")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.packageId").value("PKG001"))
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));
    }
    
    @Test
    public void testPushTrafficData_DuplicatePackageId() throws Exception {
        // 模拟重复的数据包ID
        when(trafficDataService.isPackageIdExists(anyString())).thenReturn(true);
        
        mockMvc.perform(post("/api/traffic/data/push")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("数据包ID已存在，请使用唯一标识符"));
    }
    
    @Test
    public void testPushTrafficData_InvalidData() throws Exception {
        // 创建无效的请求数据
        TrafficDataRequest invalidRequest = new TrafficDataRequest();
        invalidRequest.setPackageId(""); // 空的数据包ID
        
        mockMvc.perform(post("/api/traffic/data/push")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"));
    }
    
    @Test
    public void testBatchPushTrafficData_Success() throws Exception {
        // 模拟批量处理结果
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("totalCount", 1);
        mockResult.put("successCount", 1);
        mockResult.put("failureCount", 0);
        
        when(trafficDataService.processBatchTrafficData(any())).thenReturn(mockResult);
        
        mockMvc.perform(post("/api/traffic/data/batch-push")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(validRequest))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.totalCount").value(1))
                .andExpect(jsonPath("$.data.successCount").value(1));
    }
    
    @Test
    public void testGetPushStatus_Success() throws Exception {
        // 模拟状态查询结果
        Map<String, Object> mockStatus = new HashMap<>();
        mockStatus.put("packageId", "PKG001");
        mockStatus.put("status", "SUCCESS");
        
        when(trafficDataService.getPushStatus(anyString())).thenReturn(mockStatus);
        
        mockMvc.perform(get("/api/traffic/data/status/PKG001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.packageId").value("PKG001"))
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));
    }
    
    @Test
    public void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/traffic/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.service").value("交通数据推送服务"))
                .andExpect(jsonPath("$.data.standard").value("GA/T 1049.2-2013"));
    }
}