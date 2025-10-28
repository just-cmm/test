package com.example.maven.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * GA/T 1049.2-2013 交通数据项
 * 单个交通数据记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficDataItem {
    
    /**
     * 记录唯一标识
     */
    @NotBlank(message = "记录唯一标识不能为空")
    @Size(max = 32, message = "记录唯一标识长度不能超过32个字符")
    private String recordId;
    
    /**
     * 设备编号 - 采集设备唯一标识
     */
    @NotBlank(message = "设备编号不能为空")
    @Size(max = 20, message = "设备编号长度不能超过20个字符")
    private String deviceId;
    
    /**
     * 设备位置 - 设备安装位置描述
     */
    @NotBlank(message = "设备位置不能为空")
    @Size(max = 100, message = "设备位置长度不能超过100个字符")
    private String deviceLocation;
    
    /**
     * 经度 - 设备地理坐标经度
     */
    @NotNull(message = "经度不能为空")
    private Double longitude;
    
    /**
     * 纬度 - 设备地理坐标纬度
     */
    @NotNull(message = "纬度不能为空")
    private Double latitude;
    
    /**
     * 记录时间 - 数据记录时间
     */
    @NotNull(message = "记录时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordTime;
    
    /**
     * 车牌号码 - 车辆牌照号码
     */
    @Pattern(regexp = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$", 
             message = "车牌号码格式不正确")
    private String plateNumber;
    
    /**
     * 车辆类型 - 车辆分类
     */
    @Size(max = 20, message = "车辆类型长度不能超过20个字符")
    private String vehicleType;
    
    /**
     * 车辆颜色 - 车辆颜色
     */
    @Size(max = 10, message = "车辆颜色长度不能超过10个字符")
    private String vehicleColor;
    
    /**
     * 车速 - 车辆行驶速度（km/h）
     */
    private Integer speed;
    
    /**
     * 行驶方向 - 车辆行驶方向
     */
    @Size(max = 10, message = "行驶方向长度不能超过10个字符")
    private String direction;
    
    /**
     * 车道号 - 车辆所在车道
     */
    private Integer laneNumber;
    
    /**
     * 违法类型 - 违法行为类型
     */
    @Size(max = 50, message = "违法类型长度不能超过50个字符")
    private String violationType;
    
    /**
     * 违法代码 - 违法行为代码
     */
    @Size(max = 10, message = "违法代码长度不能超过10个字符")
    private String violationCode;
    
    /**
     * 图片URL - 相关图片存储地址
     */
    @Size(max = 500, message = "图片URL长度不能超过500个字符")
    private String imageUrl;
    
    /**
     * 视频URL - 相关视频存储地址
     */
    @Size(max = 500, message = "视频URL长度不能超过500个字符")
    private String videoUrl;
    
    /**
     * 置信度 - 数据识别置信度（0-100）
     */
    private Integer confidence;
    
    /**
     * 扩展字段 - JSON格式的扩展数据
     */
    @Size(max = 1000, message = "扩展字段长度不能超过1000个字符")
    private String extendedData;
}