package com.example.maven.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一API响应格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * 响应码
     */
    private String code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("200", "操作成功", data, System.currentTimeMillis());
    }
    
    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>("200", "操作成功", null, System.currentTimeMillis());
    }
    
    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null, System.currentTimeMillis());
    }
    
    /**
     * 参数错误响应
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>("400", message, null, System.currentTimeMillis());
    }
    
    /**
     * 服务器错误响应
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>("500", message, null, System.currentTimeMillis());
    }
    
    /**
     * 设置数据
     */
    public ApiResponse<T> setData(T data) {
        this.data = data;
        return this;
    }
}