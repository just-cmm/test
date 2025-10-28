package com.example.maven.exception;

import com.example.maven.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 全局异常处理器
 * 统一处理API异常响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.warn("参数验证失败: {}", errors);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("validationErrors", errors);
        errorData.put("errorType", "VALIDATION_ERROR");
        
        ApiResponse<Map<String, Object>> response = ApiResponse.badRequest("参数验证失败");
        response.setData(errorData);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleBindException(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.warn("数据绑定失败: {}", errors);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("bindingErrors", errors);
        errorData.put("errorType", "BINDING_ERROR");
        
        ApiResponse<Map<String, Object>> response = ApiResponse.badRequest("数据绑定失败");
        response.setData(errorData);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        
        log.warn("约束验证失败: {}", errors);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("constraintErrors", errors);
        errorData.put("errorType", "CONSTRAINT_VIOLATION");
        
        ApiResponse<Map<String, Object>> response = ApiResponse.badRequest("约束验证失败");
        response.setData(errorData);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理方法参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        
        String fieldName = ex.getName();
        String requiredType = ex.getRequiredType().getSimpleName();
        String actualValue = ex.getValue().toString();
        
        String errorMessage = String.format("参数 '%s' 类型不匹配，期望类型: %s，实际值: %s", 
                fieldName, requiredType, actualValue);
        
        log.warn("参数类型不匹配: {}", errorMessage);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("fieldName", fieldName);
        errorData.put("requiredType", requiredType);
        errorData.put("actualValue", actualValue);
        errorData.put("errorType", "TYPE_MISMATCH");
        
        ApiResponse<Map<String, Object>> response = ApiResponse.badRequest(errorMessage);
        response.setData(errorData);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        
        log.warn("非法参数: {}", ex.getMessage());
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorMessage", ex.getMessage());
        errorData.put("errorType", "ILLEGAL_ARGUMENT");
        
        ApiResponse<Map<String, Object>> response = ApiResponse.badRequest(ex.getMessage());
        response.setData(errorData);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleRuntimeException(
            RuntimeException ex) {
        
        log.error("运行时异常: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorMessage", ex.getMessage());
        errorData.put("errorType", "RUNTIME_ERROR");
        
        ApiResponse<Map<String, Object>> response = ApiResponse.serverError("系统内部错误");
        response.setData(errorData);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleGenericException(Exception ex) {
        
        log.error("未知异常: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errorMessage", "系统内部错误");
        errorData.put("errorType", "UNKNOWN_ERROR");
        
        ApiResponse<Map<String, Object>> response = ApiResponse.serverError("系统内部错误，请稍后重试");
        response.setData(errorData);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}