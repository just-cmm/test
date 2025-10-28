package com.example.maven.service;

import com.example.maven.model.TrafficDataItem;
import com.example.maven.model.TrafficDataRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 交通数据服务测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TrafficDataServiceTest {
    
    @Autowired
    private TrafficDataService trafficDataService;
    
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
    public void testProcessTrafficData_Success() {
        // 执行处理
        Map<String, Object> result = trafficDataService.processTrafficData(validRequest);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("PKG001", result.get("packageId"));
        assertEquals("SUCCESS", result.get("status"));
        assertEquals(1, result.get("processedCount"));
        assertNotNull(result.get("processedTime"));
        
        // 验证数据包ID已被记录
        assertTrue(trafficDataService.isPackageIdExists("PKG001"));
    }
    
    @Test
    public void testProcessTrafficData_EmptyPackageId() {
        // 创建空数据包ID的请求
        validRequest.setPackageId("");
        
        try {
            trafficDataService.processTrafficData(validRequest);
            fail("应该抛出IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("数据包ID不能为空", e.getMessage());
        }
    }
    
    @Test
    public void testProcessTrafficData_EmptySourceId() {
        // 创建空数据源ID的请求
        validRequest.setSourceId("");
        
        try {
            trafficDataService.processTrafficData(validRequest);
            fail("应该抛出IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("数据源ID不能为空", e.getMessage());
        }
    }
    
    @Test
    public void testProcessTrafficData_EmptyDataType() {
        // 创建空数据类型的请求
        validRequest.setDataType("");
        
        try {
            trafficDataService.processTrafficData(validRequest);
            fail("应该抛出IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("数据类型不能为空", e.getMessage());
        }
    }
    
    @Test
    public void testProcessTrafficData_EmptyDataItems() {
        // 创建空数据项的请求
        validRequest.setDataItems(Arrays.asList());
        
        try {
            trafficDataService.processTrafficData(validRequest);
            fail("应该抛出IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("数据项不能为空", e.getMessage());
        }
    }
    
    @Test
    public void testProcessTrafficData_FutureCollectTime() {
        // 创建未来采集时间的请求
        validRequest.setCollectTime(LocalDateTime.now().plusMinutes(10));
        
        try {
            trafficDataService.processTrafficData(validRequest);
            fail("应该抛出IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("数据采集时间不能晚于当前时间", e.getMessage());
        }
    }
    
    @Test
    public void testProcessTrafficData_EmptyRecordId() {
        // 创建空记录ID的数据项
        validDataItem.setRecordId("");
        
        try {
            trafficDataService.processTrafficData(validRequest);
            fail("应该抛出IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("第1条记录的记录ID不能为空", e.getMessage());
        }
    }
    
    @Test
    public void testProcessBatchTrafficData_Success() {
        // 创建批量请求
        List<TrafficDataRequest> requests = Arrays.asList(validRequest);
        
        // 执行批量处理
        Map<String, Object> result = trafficDataService.processBatchTrafficData(requests);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.get("totalCount"));
        assertEquals(1, result.get("successCount"));
        assertEquals(0, result.get("failureCount"));
        assertNotNull(result.get("processedResults"));
        assertNotNull(result.get("processedTime"));
    }
    
    @Test
    public void testGetPushStatus_Success() {
        // 先处理一个数据包
        trafficDataService.processTrafficData(validRequest);
        
        // 查询状态
        Map<String, Object> status = trafficDataService.getPushStatus("PKG001");
        
        // 验证结果
        assertNotNull(status);
        assertEquals("PKG001", status.get("packageId"));
        assertEquals("SUCCESS", status.get("status"));
    }
    
    @Test
    public void testGetPushStatus_NotFound() {
        // 查询不存在的状态
        Map<String, Object> status = trafficDataService.getPushStatus("NONEXISTENT");
        
        // 验证结果
        assertNotNull(status);
        assertEquals("NONEXISTENT", status.get("packageId"));
        assertEquals("NOT_FOUND", status.get("status"));
        assertEquals("未找到对应的数据包", status.get("message"));
    }
    
    @Test
    public void testIsPackageIdExists() {
        // 初始状态应该不存在
        assertFalse(trafficDataService.isPackageIdExists("PKG001"));
        
        // 处理数据后应该存在
        trafficDataService.processTrafficData(validRequest);
        assertTrue(trafficDataService.isPackageIdExists("PKG001"));
    }
}