package com.example.maven.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * GA/T 1049.2-2013 交通数据推送请求
 * 符合公共安全行业标准的数据结构
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficDataRequest {
    
    /**
     * 数据包标识符 - 唯一标识本次数据推送
     */
    @NotBlank(message = "数据包标识符不能为空")
    @Size(max = 32, message = "数据包标识符长度不能超过32个字符")
    private String packageId;
    
    /**
     * 数据源标识 - 标识数据来源系统
     */
    @NotBlank(message = "数据源标识不能为空")
    @Size(max = 16, message = "数据源标识长度不能超过16个字符")
    private String sourceId;
    
    /**
     * 数据采集时间 - 数据实际采集的时间
     */
    @NotNull(message = "数据采集时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime collectTime;
    
    /**
     * 数据推送时间 - 数据推送到本系统的时间
     */
    @NotNull(message = "数据推送时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime pushTime;
    
    /**
     * 数据类型 - 标识数据类型（如：车辆通行、违法记录、卡口数据等）
     */
    @NotBlank(message = "数据类型不能为空")
    @Size(max = 20, message = "数据类型长度不能超过20个字符")
    private String dataType;
    
    /**
     * 数据版本 - 数据格式版本号
     */
    @NotBlank(message = "数据版本不能为空")
    @Size(max = 10, message = "数据版本长度不能超过10个字符")
    private String dataVersion;
    
    /**
     * 数据内容 - 具体的交通数据列表
     */
    @NotNull(message = "数据内容不能为空")
    @Valid
    private List<TrafficDataItem> dataItems;
    
    /**
     * 数据签名 - 用于数据完整性验证
     */
    @Size(max = 256, message = "数据签名长度不能超过256个字符")
    private String signature;
    
    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;
}